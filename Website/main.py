import hashlib
import json
import socket
import threading
import time
from tkinter import *
from datetime import datetime
from Block import Block
from Address import Address
from Transaction import Transaction
import rsa
from binascii import hexlify
import binascii
from User import User
from flask import Flask, redirect, url_for, render_template, request, jsonify, session
from flask_mongoengine import MongoEngine
from FriendlyTransaction import FriendlyTransaction
from WalletState import WalletState

mempoolBlock = []
mempool = []
myTran = []
blockchain = []
wallet = None
client2 = None
USER1 = None
USER2 = None
turn = True
CURRENTUSER= None

global connected, diff
connected = False
diff = 5


#Serializacija Block classa
def EncodeJson(obj):
    return {"index": obj.index, "data": obj.data, "hash": obj.hash, "time": obj.time, "previousHash": obj.previousHash, "diff": obj.diff, "nonce": obj.nonce}

#serializacija Address classa
def EncodeJsonAddr(obj):
    return {"name": obj.name, "amount": obj.amount}

#serializacija Transakcije
def EncodeJsonTran(obj):
    return {"sender": obj.sender, "receiver": obj.receiver, "amountSent": obj.amountSent, "time": obj.time, "signature": obj.signature}

#spreminjanje json stringa nazaj v Block
def JsonToBlock(jsonStr):
    return Block(jsonStr["index"], jsonStr["data"], jsonStr["time"], jsonStr["hash"], jsonStr["previousHash"], jsonStr["diff"], jsonStr["nonce"])

#spreminjanje json stringa nazaj v Address
def JsonToAddr(jsonStr):
    return Address(jsonStr["name"], jsonStr["amount"])

#deserializacija transakcije
def JsonToTran(jsonStr):
    tran = Transaction(jsonStr["sender"], jsonStr["receiver"], jsonStr["amountSent"], jsonStr["signature"])
    tran.time = jsonStr["time"]
    return tran

def JsonToFriendlyTran(jsonStr):
    return FriendlyTransaction(jsonStr["sender"], jsonStr["amountSent"], jsonStr["receiver"])



def Speak(option, what, client):
    client.send(option.encode("utf-8"))
    client.recv(256)
    if option == "API_LAST" or option == "API_CHAIN" or option == "API_MINE":
        return
    elif option == "API_ADDRESS_NEW":
        tmp = json.dumps(what, default=lambda o: o.__dict__, sort_keys=False)
        client.send(tmp.encode("utf-8"))
        client.recv(256)
    elif option == "API_ADDRESS_SEND":
        tmp = json.dumps(what, default=lambda o: o.__dict__, sort_keys=False)
        client.send(tmp.encode("utf-8"))
        client.recv(256)
    elif option == "API_ADDRESS_STATE":
        tmp = json.dumps(EncodeJsonAddr(what))
        client.send(tmp.encode("utf-8"))
        client.recv(256)
    elif option == "NODES":
        tmp = str(what)
        client.send(tmp.encode("utf-8"))
        client.recv(256)
    else:
        for i in what:
            if option == "MEMPOOL_EXPAND":
                tmp = json.dumps(i, default=lambda o: o.__dict__, sort_keys=False)
            else:
                tmp = json.dumps(EncodeJson(i))
            client.send(tmp.encode("utf-8"))
            client.recv(256)
    client.send("FINISHED".encode("utf-8"))


#fsprejemanje pogovora od odjemalca
def Recieve(client, option):
    storage = []
    client.send("E".encode("utf-8"))
    while True:
        msg = client.recv(2048).decode("utf-8")
        try:
            msg = json.loads(msg)
            if option == "API_ADDRESS_NEW":
                a = JsonToTran(msg)
            elif option == "API_ADDRESS_SEND":
                a = JsonToTran(msg)
            elif option == "API_LAST":
                a = JsonToBlock(msg)
            elif option == "API_ADDRESS_STATE":
                a = JsonToAddr(msg)
            elif option == "MEMPOOL_EXPAND":
                a = JsonToTran(msg)
            elif option == "NODES":
                a = msg
            elif option == "CONNECT_WITH_ME":
                a = msg
            else:
                a = JsonToBlock(msg)
            storage.append(a)
            client.send("E".encode("utf-8"))
        except:
            pass
        if msg == "FINISHED":
            return storage


def Client(entry):
    global mempoolBlock, mempool, client2
    port = int(entry)
    client2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client2.connect(("127.0.0.1", port))
    print("Connection established")

    # API ZA NOV BLOK
    # ni. Ker bloke samo ustvarjamo z rudarjenjem (lahko maybe naredimo da oglišče začne rudarit --_('_')_--

    # API ZA DODAJANJE NASLOVA
    # Speak("API_ADDRESS_NEW", Transaction("coinbase", "NewAddressName", 1000), client2)

    # API ZA PRIDOBITEV ZADNJEGA BLOKA IZ VERIGE
    # Speak("API_LAST", None, client2)
    # print(client2.recv(256).decode("utf-8"))

    # API ZA PRIDOBITEV CELOTNE VERIGE
    # Speak("API_CHAIN", None, client2)
    # print(Recieve(client2, "API_CHAIN"))

    # API ZA NOVO TRANSAKCIJO
    # Speak("API_ADDRESS_NEW", Transaction("senderAddr", "receiverAddr", 1000), client2)

    # API ZA STANJE NA NASLOVU
    # needs an implementation

    # THIS IS USED TO SPREAD THE BLOCKCHAIN AND MEMPOOL TO OTHER NODES
    # while True:
    #    try:
    #        Speak("NODE", blockchain, client2)
    #        Speak("MEMPOOL_EXPAND", mempool, client2)
    #    except:
    #        pass
    #    print("loop")
    #    time.sleep(3)


def StartClient():
    tc = threading.Thread(target=Client)
    tc.start()
    return


def ReturnChain():
    global blockchain
    Speak("API_CHAIN", None, client2)
    blockchain = Recieve(client2, "API_CHAIN")
    return blockchain




def CreateTransaction(receiverAddr, howMuch, owner, wallet):

    senderAddr = rsa.PublicKey(int(wallet.privateKeyN), int(wallet.privateKeyE))
    sender = rsa.PrivateKey(int(wallet.privateKeyN), int(wallet.privateKeyE), int(wallet.privateKeyD), int(wallet.privateKeyP), int(wallet.privateKeyQ))

    #privateKeyN = privkey.n
    #privateKeyP = privkey.p
    #privateKeyE = privkey.e
    #privateKeyD = privkey.d
    #privateKeyQ = privkey.q

    ammount = 0

    #if turn == False:
    #    senderAddr = USER1.pubKey
    #    sender = USER1
    #elif turn == True and not USER1 == None:
    #    senderAddr = USER2.pubKey
    #    sender = USER2
    #else:
    #    senderAddr = str(recieverentry.get())

    Naslovi = []
    Naslovi.append(Address(str(receiverAddr), float(howMuch)))
    ammount = ammount + float(howMuch)
    a = Transaction(str(senderAddr.n), Naslovi, ammount, hexlify(rsa.sign(
        (f"{str(senderAddr)}{str(Naslovi)}{str(ammount)}{time}").encode("utf-8"), sender, "SHA-256")).decode("utf-8"))

    Speak("API_ADDRESS_NEW", a, client2)

    client2.close()


    ret = Transak(data=json.dumps(a, default=lambda o: o.__dict__, sort_keys=False), humanId=owner)
    ret.save()

    return

def CreateNewAddress(howMuch, publicKey):
    global turn, USER1, USER2,CURRENTUSER
    ammount = 0
    #if turn == False:
    #    receiverAddr = USER1.pubKey
    #    reciever = USER1
    #elif turn == True and not USER1 == None:
    #    receiverAddr = USER2.pubKey
    #    receiver = USER2
    #else:
    #    receiverAddr = str(recieverentry.get())

    Naslovi = []


    Naslovi.append(Address(str(publicKey.n), float(howMuch)))
    ammount = ammount + float(howMuch)

    Speak("API_ADDRESS_NEW", Transaction("coinbase", Naslovi, ammount, str(0)), client2)
    client2.close()
    return

def GenerateAddress(howMuch, owner):
    global turn, USER1, USER2
    (pubkey, privkey) = rsa.newkeys(512)

    privkey = privkey.save_pkcs1()
    print("Private key: \n" + str(privkey))

    pubkeyOg = pubkey
    pubkey = (pubkey.n, pubkey.e)
    print(f"pubkey = {pubkey}")

    # Loads the private key
    print(type(privkey))
    privkey = rsa.PrivateKey.load_pkcs1(privkey)

    #ledger.insert(END, f"{pubkeyOg}\n{privkey}\n\n")
    #ledger.see(END)

    privateKeyN = privkey.n
    privateKeyP = privkey.p
    privateKeyE = privkey.e
    privateKeyD = privkey.d
    privateKeyQ = privkey.q
    publicKey = pubkeyOg


    print("MyPrivateKey")
    wallet = Wallet(privateKeyN=str(privateKeyN), privateKeyP=str(privateKeyP), privateKeyE=str(privateKeyE), privateKeyD=str(privateKeyD),
                    privateKeyQ=str(privateKeyQ), humanId=owner)
    wallet.save()

    CreateNewAddress(howMuch, publicKey)
    #signature = hexlify(rsa.pkcs1.sign(b"kevin", privkey, 'SHA-256'))
    #print(rsa.pkcs1.verify(b"kevin", binascii.unhexlify(signature), pubkeyOg))
    #print('signature = ' + str(signature))
    #signature2 = rsa.pkcs1.sign(b"kevin", privkey, "SHA-256")
    #rsa.pkcs1.verify(b"kevin2", signature2, pubkeyOg)

def GetState(addr):
    #senderAddr = str(senderentry.get())
    senderAddr = addr

    Speak("API_ADDRESS_STATE", Address(senderAddr, 0), client2)
    time.sleep(0.03)
    addrState = Recieve(client2, "API_ADDRESS_STATE")
    addrState = addrState[0].amount
    return addrState

def startState():
    ts = threading.Thread(target=GetState)
    ts.start()
    return

def startMining():
    Speak("API_MINE", None, client2)
    return




#User selection
def CurrentUser1():
    global CURRENTUSER
    CURRENTUSER=USER1
    return

def CurrentUser2():
    global CURRENTUSER
    CURRENTUSER=USER2
    return

def DeleteCurrent():
    global CURRENTUSER
    CURRENTUSER=None
    return

def CloseConnection():
    client2.close()



app = Flask(__name__)
app.secret_key = "work_hard"

app.config['MONGODB_SETTINGS'] = {
    'db': 'blockchain',
    'host': 'localhost',
    'port': 27017
}
db = MongoEngine()
db.init_app(app)


class Human(db.Document):
    username = db.StringField()
    password = db.StringField()

class Wallet(db.Document):
    privateKeyN = db.StringField()
    privateKeyP = db.StringField()
    privateKeyE = db.StringField()
    privateKeyD = db.StringField()
    privateKeyQ = db.StringField()
    humanId = db.StringField()

class Transak(db.Document):
    data = db.StringField()
    humanId = db.StringField()


@app.route('/humanGet/', methods=['GET'])
def getHuman():
    id = session["id"]
    human = Human.objects(id=id).first()
    if not human:
        return jsonify({'error': 'data not found'})
    else:
        return human

def getWallet():
    global wallet
    id = session["id"]
    human = Wallet.objects(humanId=id).all()
    if not human:
        wallet = []
        return wallet
    else:
        wallet = human
        return wallet

@app.route('/myWallets/')
def myWallets():

      server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
      server.connect(("127.0.0.1", 65518))
      Speak("NODES", 0, server)
      time.sleep(0.1)
      otherNodes = Recieve(server, "NODES")
      for n in otherNodes:
          if n == 0:
              otherNodes.remove(n)
      print(f"printing other nodes: {otherNodes} ${type(otherNodes[0])}")

      wallets = getWallet()
      threading.Thread(target=Client, args=(otherNodes[0],)).start()
      time.sleep(0.02)
      givenWallets = []
      for w in wallets:
          state = GetState(w.privateKeyN)
          time.sleep(0.1)
          givenWallets.append(WalletState(w.privateKeyN, state))

      client2.close()

      return render_template("myWallets.html", wallets=givenWallets)



@app.route('/myTransactions/', methods=['GET','POST'])
def getTransactions():
    global myTran
    id = session["id"]
    human = Transak.objects(humanId=id).all()
    if not human:
        myTran = []
        return render_template("myTransactions.html", transactions=myTran)
    else:
        myTran = human
        print(myTran[0])
        if request.method == "GET":

            server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            server.connect(("127.0.0.1", 65518))
            Speak("NODES", 0, server)
            time.sleep(0.1)
            otherNodes = Recieve(server, "NODES")
            for n in otherNodes:
                if n == 0:
                    otherNodes.remove(n)
            print(f"printing other nodes: {otherNodes} ${type(otherNodes[0])}")
            server.close()

            threading.Thread(target=Client, args=(otherNodes[0],)).start()
            time.sleep(0.02)
            myBlocks = ReturnChain()


            confiremd = []

            for x in myTran:
                cou = 0
                for b in myBlocks:
                    if x.data in b.data:
                        confiremd.append(1)
                        cou = cou + 1
                if cou == 0:
                    confiremd.append(0)

            goodTran = []
            badTran = []
            index = 0
            for t in myTran:
                a = json.loads(t.data)
                b = JsonToFriendlyTran(a)
                b.receiver = b.receiver[0]["name"]
                if confiremd[index] == 1:
                    goodTran.append(b)
                else:
                    badTran.append(b)
                index = index + 1


            return render_template("myTransactions.html", transactions=goodTran, badTransactions=badTran, blocks=myBlocks, status=1)



@app.route('/human/', methods=['POST'])
def createHuman():
    username = request.form["username"]
    password = request.form["password"]

    user = Human(username=username, password=password)
    user.save()
    return redirect(url_for("home"))

@app.route('/human/login/', methods=['POST'])
def loginHuman():
    username = request.form["username"]
    password = request.form["password"]

    user = Human.objects(username=username, password=password).first()
    if not user:
        return jsonify({'error': 'data not found'})
    else:
        tmp = jsonify(user)
        session["id"] = json.loads(tmp.data.decode("utf-8"))['_id']['$oid']
        print(session["id"])
        session["username"] = user.username
        return redirect(url_for("home"))


@app.route("/register/", methods=["POST", "GET"])
def register():
    if request.method == "GET":
        return render_template("register.html")
    elif request.method == "POST":
        threading.Thread(target=GenerateAddress, args=(request.form["howMuch"],)).start()
        return redirect(url_for("home"))


@app.route("/login/")
def login():
    return render_template("login.html")

@app.route("/")
def home():
    global blockchain, client2, wallet, myTran

    try:
        print(session["username"])
    except:
        pass
    if session["username"] != "":
        print(len(myTran))
        return render_template("index.html", username=session["username"], )
    else:
        return render_template("index.html")


@app.route("/newAddress/", methods=["POST", "GET"])
def new():
    if request.method == "GET":
        return render_template("newAddress.html")
    elif request.method == "POST":

        server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server.connect(("127.0.0.1", 65518))
        Speak("NODES", 0, server)
        time.sleep(0.1)
        otherNodes = Recieve(server, "NODES")
        for n in otherNodes:
            if n == 0:
                otherNodes.remove(n)
        print(f"printing other nodes: {otherNodes} ${type(otherNodes[0])}")
        server.close()

        threading.Thread(target=Client, args=(otherNodes[0],)).start()
        threading.Thread(target=GenerateAddress, args=(request.form["howMuch"], session["id"])).start()
        return redirect(url_for("home"))

@app.route("/transaction/", methods=["POST", "GET"])
def transaction():
    if request.method == "GET":
        wallets = getWallet()
        return render_template("transaction.html", wallets=wallets)
    elif request.method == "POST":

        server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server.connect(("127.0.0.1", 65518))
        Speak("NODES", 0, server)
        time.sleep(0.1)
        otherNodes = Recieve(server, "NODES")
        for n in otherNodes:
            if n == 0:
                otherNodes.remove(n)
        print(f"printing other nodes: {otherNodes} ${type(otherNodes[0])}")
        server.close()

        threading.Thread(target=Client, args=(otherNodes[0],)).start()
        wallets = getWallet()
        trueWallet = None
        for w in wallets:
            if request.form["walletSelect"] == w.privateKeyN:
                trueWallet = w

        if(trueWallet != None):
            threading.Thread(target=CreateTransaction, args=(request.form["receiver"], request.form["howMuch"], session["id"], trueWallet, )).start()
        return redirect(url_for("home"))



if __name__ == "__main__":
    app.run(debug=True)




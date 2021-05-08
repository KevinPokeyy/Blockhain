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

mempoolBlock = []
mempool = []
client2 = None
USER1 = None
USER2 = None
turn = True

global connected, diff
connected = False
diff = 5

root = Tk()
root.title("Projektna naloga Api")
root.resizable(width=False, height=False)

ledger = Text(root)
ledger.grid(row=1, column=1, rowspan=5)

entry = Entry(root, width=50)
entry.grid(row=0, column=1)

# Label
var = StringVar()
label = Label(root, textvariable=var, relief=RAISED, width=50, height=2)
label.grid(row=0, column=2)
var.set("Transakcija ")

# Sender vnos
senderentry = Entry(root, width=50)
senderentry.grid(row=1, column=2)
# Reciever vnos
recieverentry = Entry(root, width=50,)
recieverentry.grid(row=2, column=2)
# Ammount vnos
ammountentry = Entry(root, width=50)
ammountentry.grid(row=3, column=2)


#Serializacija Block classa
def EncodeJson(obj):
    return {"index": obj.index, "data": obj.data, "hash": obj.hash, "time": obj.time, "previousHash": obj.previousHash, "diff": obj.diff, "nonce": obj.nonce}

#serializacija Address classa
def EncodeJsonAddr(obj):
    return {"name": obj.name, "state": obj.state}

#serializacija Transakcije
def EncodeJsonTran(obj):
    return {"sender": obj.sender, "receiver": obj.receiver, "amountSent": obj.amountSent, "time": obj.time, "signature": obj.signature}

#spreminjanje json stringa nazaj v Block
def JsonToBlock(jsonStr):
    return Block(jsonStr["index"], jsonStr["data"], jsonStr["time"], jsonStr["hash"], jsonStr["previousHash"], jsonStr["diff"], jsonStr["nonce"])

#spreminjanje json stringa nazaj v Address
def JsonToAddr(jsonStr):
    return Address(jsonStr["name"], jsonStr["state"])

#deserializacija transakcije
def JsonToTran(jsonStr):
    tran = Transaction(jsonStr["sender"], jsonStr["receiver"], jsonStr["amountSent"], jsonStr["signature"])
    tran.time = jsonStr["time"]
    return tran


def Speak(option, what, client):
    client.send(option.encode("utf-8"))
    client.recv(256)
    if option == "API_LAST" or option == "API_CHAIN":
        return
    elif option == "API_ADDRESS_NEW":
        tmp = json.dumps(EncodeJsonTran(what))
        client.send(tmp.encode("utf-8"))
        client.recv(256)
    elif option == "API_ADDRESS_SEND":
        tmp = json.dumps(EncodeJsonTran(what))
        client.send(tmp.encode("utf-8"))
        client.recv(256)
    else:
        for i in what:
            if option == "MEMPOOL_EXPAND":
                tmp = json.dumps(EncodeJsonTran(i))
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


def Client():
    global mempoolBlock, mempool, client2
    port = int(entry.get())
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
    Speak("API_CHAIN", None, client2)
    blockchain = Recieve(client2, "API_CHAIN")
    for b in blockchain:
        ledger.insert(END, f"{EncodeJson(b)}\n")
    ledger.insert(END, "\n")

    return


def ReturnLastBlock():
    Speak("API_LAST", None, client2)
    block = Recieve(client2, "API_LAST")
    ledger.insert(END, f"{json.dumps(EncodeJson(block[0]))}")
    ledger.insert(END, "\n\n")
    return


def CreateTransaction():
    receiverAddr = str(recieverentry.get())
    ammount = int(ammountentry.get())

    if turn == False:
        senderAddr = USER1.pubKey
        sender = USER1
    elif turn == True and not USER1 == None:
        senderAddr = USER2.pubKey
        sender = USER2
    else:
        senderAddr = str(recieverentry.get())

    Speak("API_ADDRESS_NEW", Transaction(str(senderAddr), str(receiverAddr), ammount, str(hexlify(rsa.sign(
        bytes((f"{str(senderAddr)}{str(receiverAddr)}{str(ammount)}").encode("utf-8")), sender.priKey, "SHA-256")))), client2)
    return

def CreateNewAddress():
    global turn, USER1, USER2
    ammount = int(ammountentry.get())
    if turn == False:
        receiverAddr = USER1.pubKey
        reciever = USER1
    elif turn == True and not USER1 == None:
        receiverAddr = USER2.pubKey
        receiver = USER2
    else:
        receiverAddr = str(recieverentry.get())

    Speak("API_ADDRESS_NEW", Transaction("coinbase", str(receiverAddr), ammount, str(0)), client2)
    return

def GenerateAddress():
    global turn, USER1, USER2
    (pubkey, privkey) = rsa.newkeys(512)

    privkey = privkey.save_pkcs1()
    print("Private key: \n" + str(privkey))

    pubkeyOg = pubkey
    pubkey = (pubkey.n, pubkey.e)
    print(f"pubkey = {pubkey}")

    # Loads the private key
    privkey = rsa.PrivateKey.load_pkcs1(privkey)

    ledger.insert(END, f"{pubkeyOg}\n{privkey}\n\n")
    ledger.see(END)

    if turn == True:
        USER1 = User(pubkeyOg, privkey)
        turn = False
    else:
        USER2 = User(pubkeyOg, privkey)
        turn = True

    #signature = hexlify(rsa.pkcs1.sign(b"kevin", privkey, 'SHA-256'))
    #print(rsa.pkcs1.verify(b"kevin", binascii.unhexlify(signature), pubkeyOg))
    #print('signature = ' + str(signature))
    #signature2 = rsa.pkcs1.sign(b"kevin", privkey, "SHA-256")
    #rsa.pkcs1.verify(b"kevin2", signature2, pubkeyOg)



clientButton = Button(root, text="Connect", command=StartClient)
clientButton.grid(row=0, column=0)

returnChainButton = Button(root, text="Get Blockchain", command=ReturnChain)
returnChainButton.grid(row=1, column=0)

returnLastBlockButton = Button(root, text="Last Block", command=ReturnLastBlock)
returnLastBlockButton.grid(row=2, column=0)

createNextBlockButton = Button(root, text="Send Transaction", command=CreateTransaction)
createNextBlockButton.grid(row=3, column=0)

createNextBlockButton = Button(root, text="Send Address", command=CreateNewAddress)
createNextBlockButton.grid(row=4, column=0)

createNextBlockButton = Button(root, text="Generate Address", command=GenerateAddress)
createNextBlockButton.grid(row=5, column=0)


root.mainloop()






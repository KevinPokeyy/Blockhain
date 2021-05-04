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


blockchain = []
nodes = []
mempoolBlock = []
mempool = []
clients = []
currentTranId = 0
coinbase = Address("coinbase", 21000000)

global connected, diff
connected = False
diff = 5

root = Tk()
root.title("Projekt Naloga 1")
root.resizable(width=False, height=False)

ledger = Text(root)
ledger.grid(row=1, column=0, columnspan=3)

hashText = Text(root)
hashText.grid(row=1, column=4, columnspan=2)

port = Label(root, text="")
port.grid(row=0, column=2)

entry = Entry(root, width=50)
entry.grid(row=0, column=5)

#preverjanje ustreznosti trenutne težavnosti
def CurrentDiff():
    global diff
    previousAdjustmentBlock = blockchain[len(blockchain) - 10]
    timeExpected = 10 * 10

    time1 = previousAdjustmentBlock.time[11:19]
    time2 = blockchain[len(blockchain) - 1].time[11:19]

    h1 = time1[0:2]
    m1 = time1[3:5]
    s1 = time1[6:8]

    h2 = time2[0:2]
    m2 = time2[3:5]
    s2 = time2[6:8]

    h1 = int(h1)
    m1 = int(m1)
    s1 = int(s1)

    h2 = int(h2)
    m2 = int(m2)
    s2 = int(s2)

    timeTaken = (s2 - s1) + (m2 - m1) * 60 + (h2 -h1) * 3600

    if timeTaken < (timeExpected / 2):
        diff = diff + 1
    elif timeTaken > (timeExpected * 2):
        diff = diff - 1
    return

#pridobi zadnji id transakcije v blockchainu
def GetLastId():
    global currentTranId
    tmp = 0
    for b in blockchain:
        if not b.data == "None":
            print(b.data)


#fpogovor z strežnikom
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
    if option == "API_LAST":
        return
    while True:
        msg = client.recv(512).decode("utf-8")
        try:
            msg = json.loads(msg)
            if option == "API_ADDRESS_NEW":
                a = JsonToTran(msg)
            elif option == "API_ADDRESS_SEND":
                a = JsonToTran(msg)
            elif option == "MEMPOOL_EXPAND":
                a = JsonToTran(msg)
            else:
                a = JsonToBlock(msg)
            storage.append(a)
            client.send("E".encode("utf-8"))
        except:
            pass
        if msg == "FINISHED":
            return storage

#validiranje blocka za ustreznega
def Validate(currentBLock, prevBlock):
    if currentBLock.previousHash == prevBlock.hash and CalculateHash(currentBLock.index, currentBLock.data, currentBLock.time, currentBLock.previousHash, currentBLock.diff, currentBLock.nonce) == currentBLock.hash:
        return True
    else:
        return False


#Serializacija Block classa
def EncodeJson(obj):
    return {"index": obj.index, "data": obj.data, "hash": obj.hash, "time": obj.time, "previousHash": obj.previousHash, "diff": obj.diff, "nonce": obj.nonce}

#serializacija Address classa
def EncodeJsonAddr(obj):
    return {"name": obj.name, "state": obj.state}

#serializacija Transakcije
def EncodeJsonTran(obj):
    return {"sender": obj.sender, "receiver": obj.receiver, "amountSent": obj.amountSent, "time": obj.time, "hash": obj.hash}

#spreminjanje json stringa nazaj v Block
def JsonToBlock(jsonStr):
    return Block(jsonStr["index"], jsonStr["data"], jsonStr["time"], jsonStr["hash"], jsonStr["previousHash"], jsonStr["diff"], jsonStr["nonce"])

#spreminjanje json stringa nazaj v Address
def JsonToAddr(jsonStr):
    return Address(jsonStr["name"], jsonStr["state"])

#deserializacija transakcije
def JsonToTran(jsonStr):
    tran = Transaction(jsonStr["sender"], jsonStr["receiver"], jsonStr["amountSent"])
    tran.time = jsonStr["time"]
    tran.hash = jsonStr["hash"]
    return tran


#primerjanje močnejšega blockchaina
def CmpChains(imposter):
    currentStrength = 0
    imposterStrength = 0
    for i in blockchain:
        currentStrength = currentStrength + pow(2, i.diff)
    for i in imposter:
        imposterStrength = imposterStrength + pow(2, i.diff)

    if imposterStrength > currentStrength:
        return True
    else:
        return False

#calculiranje hasha
def CalculateHash(index, data, time, previousHash, diff, nonce):
    return hashlib.sha256((str(index) + data + str(time) + previousHash + str(diff) + str(
        nonce)).encode()).hexdigest()


#iskanje validnega hasha z nonce in diff
def FindValidHash(index, data, time, previousHash):
    global diff
    nonce = 0
    counter = 0
    while True:
        validHash = True
        hash = CalculateHash(index, data, time, previousHash, diff, nonce)
        counter = counter + 1
        if counter == 1000:
            hashText.insert(END, f"\n{hash}")
            hashText.see(END)
            counter = 0
        for i in range(diff):
            if hash[i] != '0':
                validHash = False
        if validHash:
            return Block(index, data, time, hash, previousHash, diff, nonce)
        else:
            nonce = nonce + 1

#rudarjenje
def Mine():
    global client2, connected, port, diff, mempoolBlock
    while True:
        currentIndex = len(blockchain)
        currentData = "None"
        if not len(mempool) == 0:
            currentData = str(EncodeJsonTran(mempool[0]))
        currentTime = datetime.now()

        if not blockchain:
            currentPrevHash = "0"
        else:
            currentPrevHash = blockchain[len(blockchain) - 1].hash


        a = FindValidHash(currentIndex, currentData, currentTime, currentPrevHash)
        inBlockchain = False
        for b in blockchain:
            if a.data in b.data and not a.data == "None":
                inBlockchain = True

        if not blockchain and not inBlockchain:
            blockchain.append(a)
            ledger.insert(END, f"\nIndex: {a.index}\ndiff: {a.diff}\nData: {a.data}\nHash: {a.hash}\nTimestamp: {a.time}\nNonce: {a.nonce}\nPrevHash: {a.previousHash}\n")
            ledger.see(END)
        elif not inBlockchain:
            if Validate(a, blockchain[len(blockchain) - 1]):
                blockchain.append(a)
                ledger.insert(END, f"\nIndex: {a.index}\ndiff: {a.diff}\nData: {a.data}\nHash: {a.hash}\nTimestamp: {a.time}\nNonce: {a.nonce}\nPrevHash: {a.previousHash}\n")
                ledger.see(END)
                print("Block has been added")
                print(a.hash)
                print(mempool)
                if not currentData == "None":
                    mempool.remove(mempool[0])
            else:
                print("Invalid block")
        #if len(blockchain) % 10 == 0:
            #CurrentDiff()


#obravnana z individualnimi clienti
def HandleClient(client, address):
    global blockchain, diff, mempoolBlock, mempool, clients
    while True:
        msg = client.recv(512).decode("utf-8")
        if msg == "NODE":
            if not client in clients:
                clients.append(client)
            imposter = Recieve(client, "NODE")
            if CmpChains(imposter):
                blockchain = imposter.copy()
                diff = blockchain[len(blockchain) - 1].diff
                imposter.clear()
                ledger.delete('1.0', END)
                for a in blockchain:
                    ledger.insert(END, f"\nIndex: {a.index}\ndiff: {a.diff}\nData: {a.data}\nHash: {a.hash}\nTimestamp: {a.time}\nNonce: {a.nonce}\nPrevHash: {a.previousHash}\n")
                    ledger.see(END)
            else:
                imposter.clear()

        #širjenje mempool po omrežju
        elif msg == "MEMPOOL_EXPAND":
            imposterMem = Recieve(client, "MEMPOOL_EXPAND")
            print(len(imposterMem))
            print(imposterMem)
            for i in imposterMem:
                inBlockchain = False
                for b in blockchain:
                    if i.hash in b.data:
                        inBlockchain = True
                if not inBlockchain:
                    inMempool = 0
                    for t in mempool:
                        if t.hash == i.hash:
                            inMempool = inMempool + 1
                    if inMempool <= 0:
                        mempool.append(i)
                    elif inMempool > 1:
                        mempool.remove(i)




        #pridobitev zadnjega bloka
        elif msg == "API_LAST":
            Recieve(client, "API_LAST")
            tmp = json.dumps(EncodeJson(blockchain[len(blockchain) - 1]))
            client.send(tmp.encode("utf-8"))


        #pridobitev cele verige
        elif msg == "API_CHAIN":
            Speak("NODE", blockchain, client)


        #Ustvarjanje zahteve novega bloka
        elif msg == "API_BLOCK_NEW":
            all = ""
            for i in imposter:
                all = all + i
            mempoolBlock.append(all)

        #ustvarjanje novega naslova z stanjem
        elif msg == "API_ADDRESS_NEW":
            newAddr = Recieve(client, "API_ADDRESS_NEW")
            print("heard2")
            mempool.append(newAddr[0])

            print(len(mempool))


        #ustvarjnanje transakcije
        elif msg == "API_ADDRESS_SEND":
            tran = Recieve(client, "API_ADDRESS_SEND")
            mempool.append(tran[0])
            for c in clients:
                try:
                    Speak("MEMPOOL_EXPAND", mempool, c)
                except:
                    pass
            print(len(mempool))






#oprtje porta za streženje
def Server():
    global sock
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.bind(("127.0.0.1", 0))
    print("Listening on port:" + str(sock.getsockname()))
    port.configure(text=sock.getsockname())
    while True:
        sock.listen()
        client, address = sock.accept()
        print("Client has connected")
        threading.Thread(target=HandleClient, args=(client, address, )).start()


#oprtje porta za pošiljanje
def Client():
    global mempoolBlock, mempool
    port = int(entry.get())
    client2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client2.connect(("127.0.0.1", port))
    print("Connection established")

    #API ZA NOV BLOK
    #ni. Ker bloke samo ustvarjamo z rudarjenjem (lahko maybe naredimo da oglišče začne rudarit --_('_')_--

    #API ZA DODAJANJE NASLOVA
    #Speak("API_ADDRESS_NEW", Transaction("coinbase", "NewAddressName", 1000), client2)

    #API ZA PRIDOBITEV ZADNJEGA BLOKA IZ VERIGE
    #Speak("API_LAST", None, client2)
    #print(client2.recv(256).decode("utf-8"))

    #API ZA PRIDOBITEV CELOTNE VERIGE
    #Speak("API_CHAIN", None, client2)
    #print(Recieve(client2, "API_CHAIN"))

    #API ZA NOVO TRANSAKCIJO
    #Speak("API_ADDRESS_NEW", Transaction("senderAddr", "receiverAddr", 1000), client2)

    #API ZA STANJE NA NASLOVU
    #needs an implementation

    while True:
        try:
            Speak("NODE", blockchain, client2)
            Speak("MEMPOOL_EXPAND", mempool, client2)
        except:
            pass
        print("loop")
        time.sleep(3)


def StartClient():
    tc = threading.Thread(target=Client)
    tc.start()
    return

def StartServer():
    ts = threading.Thread(target=Server)
    ts.start()
    return

def StartMining():
    ts = threading.Thread(target=Mine)
    ts.start()
    return


clientButton = Button(root, text="Client Mode", command=StartClient)
clientButton.grid(row=0, column=4)

serverButton = Button(root, text="Server Mode", command=StartServer)
serverButton.grid(row=0, column=1)

MineButton = Button(root, text="Mine", command=StartMining)
MineButton.grid(row=0, column=0)

root.mainloop()





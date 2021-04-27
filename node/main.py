import hashlib
import json
import socket
import threading
import time
from tkinter import *
from datetime import datetime
from Block import Block

blockchain = []
global connected, diff
connected = False
diff = 5

root = Tk()
root.title("Vaja5")

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


#validiranje blocka za ustreznega
def Validate(currentBLock, prevBlock):
    if currentBLock.previousHash == prevBlock.hash and CalculateHash(currentBLock.index, currentBLock.data, currentBLock.time, currentBLock.previousHash, currentBLock.diff, currentBLock.nonce) == currentBLock.hash:
        return True
    else:
        return False


#Serializacija Block classa
def EncodeJson(obj):
    return {"index": obj.index, "data": obj.data, "hash": obj.hash, "time": obj.time, "previousHash": obj.previousHash, "diff": obj.diff, "nonce": obj.nonce}


#spreminjanje json stringa nazaj v Block
def JsonToBlock(jsonStr):
    return Block(jsonStr["index"], jsonStr["data"], jsonStr["time"], jsonStr["hash"], jsonStr["previousHash"], jsonStr["diff"], jsonStr["nonce"])


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
        hash = CalculateHash(index, data, time, previousHash ,diff, nonce)
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
    global client2, connected, port, diff
    while True:
        currentIndex = len(blockchain)
        currentData = "Block" + str(len(blockchain))
        currentTime = datetime.now()

        if not blockchain:
            currentPrevHash = "0"
        else:
            currentPrevHash = blockchain[len(blockchain) - 1].hash


        a = FindValidHash(currentIndex, currentData, currentTime, currentPrevHash)

        if not blockchain:
            blockchain.append(a)
            ledger.insert(END, f"\nIndex: {a.index}\ndiff: {a.diff}\nData: {a.data}\nHash: {a.hash}\nTimestamp: {a.time}\nNonce: {a.nonce}\nPrevHash: {a.previousHash}\n")
            ledger.see(END)
        else:
            if Validate(a, blockchain[len(blockchain) - 1]):
                blockchain.append(a)
                ledger.insert(END, f"\nIndex: {a.index}\ndiff: {a.diff}\nData: {a.data}\nHash: {a.hash}\nTimestamp: {a.time}\nNonce: {a.nonce}\nPrevHash: {a.previousHash}\n")
                ledger.see(END)
                print("Block has been added")
                print(a.hash)
            else:
                print("Invalid block")
        if len(blockchain) % 10 == 0:
            CurrentDiff()


#obravnana z individualnimi clienti
def HandleClient(client, address):
    global blockchain, diff
    imposter = []
    while True:
        msg = client.recv(512).decode("utf-8")
        if msg == "FINISHED":
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

        try:
            msg = json.loads(msg)
            a = JsonToBlock(msg)
            imposter.append(a)
            client.send("E".encode("utf-8"))
        except:
            pass


#oprtje porta za streženje
def Server():
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.bind(("127.0.0.1", 0))
    print("Listening on port:" + str(sock.getsockname()))
    port.configure(text=sock.getsockname())
    sock.listen()
    client, address = sock.accept()
    print("Client has connected")
    threading.Thread(target=HandleClient, args=(client, address, )).start()


#oprtje porta za pošiljanje
def Client():
    port = int(entry.get())
    client2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client2.connect(("127.0.0.1", port))
    print("Connection established")

    while True:
        for i in blockchain:
            tmp = json.dumps(EncodeJson(i))
            client2.send(tmp.encode("utf-8"))
            client2.recv(256)
        client2.send("FINISHED".encode("utf-8"))
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





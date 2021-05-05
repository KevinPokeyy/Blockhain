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


mempoolBlock = []
mempool = []

global connected, diff
connected = False
diff = 5

root = Tk()
root.title("Projektna naloga Api")
root.resizable(width=False, height=False)

ledger = Text(root)
ledger.grid(row=0, column=1, rowspan=5)

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

def ReturnChain():
    tc = threading.Thread(target=Speak("API_CHAIN", None, client2))
    tc.start()
    #print(Recieve(client2, "API_CHAIN"))
    return    

def ReturnLastBlock():
    tc = threading.Thread(target=Speak("API_LAST", None, client2))
    tc.start()
    #print(client2.recv(256).decode("utf-8"))
    return      

def NextBlock():
    tc = threading.Thread(target=Speak("API_ADDRESS_NEW", Transaction("senderAddr", "receiverAddr", 1000), client2) )
    tc.start()
    #print(client2.recv(256).decode("utf-8"))
    return  
    



clientButton = Button(root, text="Client Mode", command=StartClient)
clientButton.grid(row=0, column=0)

returnChainButton=Button(root,text="Vrni celotno verigo", command=ReturnChain)
returnChainButton.grid(row=1, column=0)

returnLastBlockButton=Button(root,text="Vrni zadnji blok", command=ReturnLastBlock)
returnLastBlockButton.grid(row=2, column=0)

createNextBlockButton=Button(root,text="Ustvari nasljedeni blok", command=NextBlock)
createNextBlockButton.grid(row=3, column=0)

switchChainButton=Button(root,text="Zamenjaj verigo", command=StartClient)
switchChainButton.grid(row=4, column=0)

addBlockButton=Button(root,text="Dodaj blok v verigo", command=StartClient)
addBlockButton.grid(row=5, column=0)

root.mainloop()

import socket
import threading


nodes = []
port = 65519

#fpogovor z strežnikom
def Speak(option, what, client):
    for i in what:
        tmp = i
        print(tmp)
        client.send(tmp.encode("utf-8"))
        client.recv(256)
    client.send("FINISHED".encode("utf-8"))

#fsprejemanje pogovora od odjemalca
def Recieve(client, option):
    storage = []
    client.send("E".encode("utf-8"))
    a = ""
    if option == "API_LAST":
        return
    while True:
        msg = client.recv(512).decode("utf-8")
        if msg == "FINISHED":
            return storage
        try:
            if option == "NODES":
                a = msg
                print(a)
            storage.append(a)
            client.send("E".encode("utf-8"))
        except:
            pass
        if msg == "FINISHED":
            return storage

#obravnana z individualnimi clienti
def HandleClient(client, address):
    global blockchain, diff, mempoolBlock, mempool, clients
    while True:
        msg = client.recv(512).decode("utf-8")
        if msg == "NODES":
            print("I have recieved a node msg")
            extra = Recieve(client, "NODES")
            nodes.append(extra[0])
            client.recv(512).decode("utf-8")
            Speak("NODES", nodes, client)
            print("returned")
            return



#oprtje porta za streženje
def Server():
    global sock
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.bind(("127.0.0.1", port))
    print("Listening on port:" + str(sock.getsockname()))
    while True:
        sock.listen()
        client, address = sock.accept()
        print("Client has connected")
        threading.Thread(target=HandleClient, args=(client, address, )).start()

Server()

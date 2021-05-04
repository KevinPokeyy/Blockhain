from datetime import datetime
import hashlib

class Transaction:
    def __init__(self, sender, receiver, amountSent):
        self.sender = sender
        self.receiver = receiver
        self.amountSent = amountSent
        self.time = str(datetime.now())
        self.hash = hashlib.sha256((sender + receiver + str(self.amountSent) + str(self.time)).encode()).hexdigest()

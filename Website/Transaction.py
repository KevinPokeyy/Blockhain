from datetime import datetime
import hashlib

class Transaction:
    def __init__(self, sender, receiver, amountSent, signature):
        self.sender = sender
        self.receiver = receiver
        self.amountSent = amountSent
        self.time = str(datetime.now())
        self.signature = signature

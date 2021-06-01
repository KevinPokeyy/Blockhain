class Block:
    def __init__(self, index, data, time, hash, previousHash, diff, nonce):
        self.index = index
        self.data = data
        self.hash = hash
        self.time = str(time)
        self.previousHash = previousHash
        self.diff = diff
        self.nonce = nonce






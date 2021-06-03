fun main(){
    var scanner = Scanner()
    scanner.readFile("primer.txt")

    scanner.getTokens()

    var analizer = Analizer(scanner.tokens)
    analizer.PROGRAM()

}
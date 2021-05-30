fun main(){
    var scanner = Scanner()
    scanner.readFile("/home/peeper/Desktop/testL.txt")
    println(scanner.everything)
    scanner.getTokens()
    for(t in scanner.tokens){
        t.printToken()
    }
    var analizer = Analizer(scanner.tokens)
    analizer.PROGRAM()

}
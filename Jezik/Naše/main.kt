
/*
    procedure PROGRAM
        call BODY

    procedure BODY
        if token = for then NextToken
            call RESERVED_FOR
            call BODY
        else if token = if then NextToken
            call RESERVED_IF
            call BODY
        else if token = variable then NextToken
            if token = '=' then NextToken
                call OPERATIONS
                call Body
            else error

    procedure OPERATIONS
        call OPERATOR
            if token = operator then NextToken
                call OPERATIONS
            else error

   procedure OPERATOR
        if token = number then NextToken
        else if token = variable then NextToken
        else error

   procedure CONDITION
        call OPERATIONS
        if token = checker then NextToken
            call OPERATIONS

   procedure RESERVED_IF
        call CONDITION
        call BODY

   procedure RESERVED_FOR
        call FOR_CONDITION
        call BODY


   procedure FOR_CONDITION
        if token = '(' then NextToken
            call CONDITION
            call OPERATIONS
            if token = ') then NextToken
            else error
        else error
 */

class Analizer(var tokens: ArrayList<Token>){

    var index = 0;
    var ok = true


    fun PROGRAM(){
        tokens.add(Token("END", "END"))
        ok = true
        BODY()
        if(ok){
            println("true")
        }
        else{
            println("false")
        }
    }

    fun BODY(){
        if (tokens[index].lexem == "for"){
            index++
            RESERVED_FOR()
            BODY()
        }
        else if (tokens[index].lexem == "if"){
            index++
            RESERVED_IF()
            BODY()
        }
        else if (tokens[index].symbol == "variable"){
            index++
            if(tokens[index].lexem == "="){
                index++
                OPERATIONS()
                BODY()
            }
            else{
                if(tokens[index].symbol != "END") {
                    ok = false
                }
            }
        }
    }

    fun OPERATIONS(){
        OPERATOR()
        if(tokens[index].symbol == "operator"){
            index++
            OPERATIONS()
        }

    }

    fun OPERATOR(){
        if(tokens[index].symbol == "variable"){
            index++
        }
        else if(tokens[index].symbol == "number"){
            index++
        }
        else {
            if(tokens[index].symbol != "END") {
                ok = false
            }
        }
    }

    fun CONDITION(){
        OPERATIONS()
        if(tokens[index].symbol == "checker"){
            index++
            OPERATIONS()
        }

    }

    fun RESERVED_IF(){
        if(tokens[index].lexem == "("){
            index++
            CONDITION()
            if(tokens[index].lexem == ")"){
                index++
                if(tokens[index].lexem == "{"){
                    index++
                    BODY()
                    if(tokens[index].lexem == "}"){
                        index++
                    }
                    else{
                        ok = false
                    }
                }
                else{
                    if(tokens[index].symbol != "END") {
                        ok = false
                    }
                }
            }
            else{
                if(tokens[index].symbol != "END") {
                    ok = false
                }
            }
        }
        else{
            if(tokens[index].symbol != "END") {
                ok = false
            }
        }
    }

    fun RESERVED_FOR(){
        FOR_CONDITION()
        if(tokens[index].lexem == "{"){
            index++
            BODY()
            if(tokens[index].lexem == "}"){
                index++
            }
            else{
                ok = false
            }
        }
        else{
            if(tokens[index].symbol != "END") {
                ok = false
            }
        }
    }

    fun FOR_CONDITION(){
        if(tokens[index].lexem == "(") {
            index++
            CONDITION()
            if (tokens[index].lexem == "(") {
                index++
                OPERATIONS()
                if(tokens[index].lexem == ")"){
                    index++
                    if(tokens[index].lexem == ")"){
                        index++
                    }
                    else{
                        if(tokens[index].symbol != "END") {
                            ok = false
                        }
                    }
                }
                else{
                    if(tokens[index].symbol != "END") {
                        ok = false
                    }
                }
            }
            else{
                if(tokens[index].symbol != "END") {
                    ok = false
                }
            }
        }
        else{
            if(tokens[index].symbol != "END") {
                ok = false
            }
        }
    }




}

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
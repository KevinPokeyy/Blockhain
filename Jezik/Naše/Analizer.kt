
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
                call BODY
            else error
        else if token = action then NextToken
            call ACTION
            call BODY

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

   procedure ACTION
        if token = "BuyItem" then NextToken
            if token = ( then NextToken
                call BUY_ITEM_ARGS
                if token = ) then NextToken
                else error
            else error
        else if token = "SellItem" then NextToken
            if token = ( then NextToken
                call SELL_ITEM_ARGS
                if token = ) then NextToken
                else error
            else error
        else if token = "TransferMoney" then NextToken
            if token = ( then NextToken
                call TRANSFER_MONEY_ARGS
                if token = ) then NextToken
                else error
            else error
        else if token = "TransferItem" then NextToken
            if token = ( then NextToken
                call TRANSFER_ITEM_ARGS
                if token = ) then NextToken
                else error
            else error
        else error

   procedure BUY_ITEM_ARGS
        if token = variable then NextToken
            if token = , then NextToken
                call OPERATOR
                if token = , then NextToken
                    if token = variable then NextToken
                    else error
                else error
            else error
        else error

   procedure SELL_ITEM_ARGS
        if token = variable then NextToken
            if token = , then NextToken
                if token = variable then NextToken
                else error
            else error
        else error

   procedure TRANSFER_MONEY_ARGS
        if token = variable then NextToken
            if token = , then NextToken
                if token = variable then NextToken
                    if token = , then NextToken
                        call OPERATOR
                    else error
                else error
            else error
        else error

   procedure TRANSFER_ITEM_ARGS
        if token = variable then NextToken
            if token = , then NextToken
                if token = variable then NextToken
                    if token = , then NextToken
                        if token = variable then NextToken
                        else error
                    else error
                else error
            else error
        else error

 */

class Analizer(var tokens: ArrayList<Token>){

    var index = 0;
    var ok = true
    var varTokens = arrayListOf<Token>()



    fun PROGRAM(){
        tokens.add(Token("END", "END"))
        ok = true
        if(tokens[index].lexem == "{"){
            index++
            BODY()
            if(tokens[index].lexem == "}"){
                index++
            }
            else {
                if(tokens[index].symbol != "END") {
                    ok = false
                }
            }
        }
        else {
            if(tokens[index].symbol != "END") {
                ok = false
            }
        }
        if(ok){
            println("CODE IS VALID")
        }
        else{
            println("INVALID CODE")
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
        else if (tokens[index].symbol == "action"){
            index++
            ACTION()
            BODY()
        }
    }

    fun OPERATIONS(){

        if(tokens[index].lexem == "\""){
            index++
            if(tokens[index].symbol == "variable"){
                index++
                if(tokens[index].lexem == "\""){
                    index++
                    return
                }
                else {
                    if(tokens[index].symbol != "END") {
                        ok = false
                    }
                }
            }
            else {
                if(tokens[index].symbol != "END") {
                    ok = false
                }
            }
        }
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
                if (tokens[index].symbol == "variable") {
                    index++
                    if(tokens[index].lexem == "=") {
                        index++
                        OPERATIONS()
                        if (tokens[index].lexem == ")") {
                            index++
                            if (tokens[index].lexem == ")") {
                                index++
                            } else {
                                if (tokens[index].symbol != "END") {
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

    fun ACTION(){
        if (tokens[index].lexem == "BuyItem"){
            index++
            if(tokens[index].lexem == "("){
                index++
                BUY_ITEM_ARGS()
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

        else if (tokens[index].lexem == "SellItem"){
            index++
            if(tokens[index].lexem == "("){
                index++
                SELL_ITEM_ARGS()
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

        else if (tokens[index].lexem == "TransferMoney"){
            index++
            if(tokens[index].lexem == "("){
                index++
                TRANSFER_MONEY_ARGS()
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

        else if (tokens[index].lexem == "TransferItem"){
            index++
            if(tokens[index].lexem == "("){
                index++
                TRANSFER_ITEM_ARGS()
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

    fun BUY_ITEM_ARGS(){
        if(tokens[index].symbol == "variable"){
            index++
            if (tokens[index].lexem == ","){
                index++
                OPERATOR()
                if (tokens[index].lexem == ","){
                    index++
                    if(tokens[index].symbol == "variable"){
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

    fun SELL_ITEM_ARGS() {
        if (tokens[index].symbol == "variable") {
            index++
            if (tokens[index].lexem == ",") {
                index++
                if (tokens[index].symbol == "variable") {
                    index++
                } else {
                    if (tokens[index].symbol != "END") {
                        ok = false
                    }
                }
            } else {
                if (tokens[index].symbol != "END") {
                    ok = false
                }
            }
        } else {
            if (tokens[index].symbol != "END") {
                ok = false
            }
        }
    }

    fun TRANSFER_MONEY_ARGS(){
        if(tokens[index].symbol == "variable"){
            index++
            if (tokens[index].lexem == ","){
                index++
                if (tokens[index].symbol == "variable"){
                    index++
                    if(tokens[index].lexem == ","){
                        index++
                        OPERATOR()
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

    fun TRANSFER_ITEM_ARGS(){
        if(tokens[index].symbol == "variable"){
            index++
            if (tokens[index].lexem == ","){
                index++
                if (tokens[index].symbol == "variable"){
                    index++
                    if(tokens[index].lexem == ","){
                        index++
                        if(tokens[index].symbol == "variable"){
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
        else{
            if(tokens[index].symbol != "END") {
                ok = false
            }
        }
    }

}
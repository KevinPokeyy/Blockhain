

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
    var variables: ArrayList<Variable> = arrayListOf<Variable>()
    var SellItem_actions: ArrayList<SellItem> = arrayListOf<SellItem>()


    fun getVariableValue(name: String) : Double?{
        for(v in variables){
            if(v.name == name){
                if(v.value is Double){
                    return v.value
                }
            }
        }
        return null
    }


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
            println()
            for(v in variables){
                println("NAME: ${v.name}, VALUE: ${v.value}")
            }
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
            //pridobitev imena spremenljivke
            val name = tokens[index].lexem
            index++
            if(tokens[index].lexem == "="){
                index++
                //pridobitev vrednosti spremenljivke
                var value = OPERATIONS()
                //kreiranje spremenljivke
                variables.add(Variable(name, value))
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

    fun OPERATIONS() : Any {

        if(tokens[index].lexem == "\""){
            index++
            if(tokens[index].symbol == "variable"){
                var string = tokens[index].lexem
                index++
                if(tokens[index].lexem == "\""){
                    index++
                    return string
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
        var value = OPERATOR()
        if(tokens[index].symbol == "operator"){

            index++
            var operationsValue = OPERATIONS()
            if (operationsValue is Number){
                return value + operationsValue.toDouble()
            }
        }
        return value
    }

    fun OPERATOR() : Double {
        if(tokens[index].symbol == "variable"){
            var value = getVariableValue(tokens[index].lexem)
            index++
            if(value != null) {
                return value
            }
            return 0.toDouble()
        }
        else if(tokens[index].symbol == "number"){
            index++
            return tokens[index - 1].lexem.toDouble()
        }
        else {
            if(tokens[index].symbol != "END") {
                ok = false
            }
        }
        return -1.1
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
                /////////////////////////////
                var list=SELL_ITEM_ARGS()
                if(tokens[index].lexem == ")"){
                    index++
                    /////////////////////////// ustvaris nov objekt SellItem in ga das v seznam vseh
                    var action_sellitem=SellItem("SellItem",list[0],list[1])
                    SellItem_actions.add(action_sellitem)


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
    ///////////////// returnas list
    fun SELL_ITEM_ARGS(): MutableList<Variable> {
        var list: MutableList<Variable> = mutableListOf<Variable>();
        if (tokens[index].symbol == "variable") {
            ///////////////////////////////// shranis variable za playera
            var player=Variable("Player",tokens[index].lexem)
            index++
            if (tokens[index].lexem == ",") {
                index++
                if (tokens[index].symbol == "variable") {
                    //////////////////////////////// shranis variable za item
                    var item=Variable("Item",tokens[index].lexem)
                    index++
                    //////////////////////////////// das oboje v seznam

                    list.add(player)
                    list.add(item)

                    return list

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
        return list
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

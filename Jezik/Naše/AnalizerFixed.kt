

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
    var mainBody: Body = Body()




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


    fun PROGRAM() : Contract?{
        tokens.add(Token("END", "END"))
        ok = true
        var contract: Contract? =null
        if(tokens[index].lexem == "{"){
            index++
            var b = Body()
            val body=BODY(b)
            if(tokens[index].lexem == "}"){
                index++
                contract=Contract(body)
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
            for(i in  contract!!.body.action){
                for(j in i.BuyItems){
                    println("Player: ${j.player.value}, Item: ${j.item.value}")
                }
            }
            return contract


        }
        else{
            println("INVALID CODE")
            return null
        }
    }

    fun BODY(body: Body):Body{
        if (tokens[index].lexem == "for"){
            index++
            val for_statement=RESERVED_FOR()
            body.forstatements.add(for_statement!!)
            BODY(body)
        }
        else if (tokens[index].lexem == "if"){
            index++
            val if_statement=RESERVED_IF()
            body.ifstatements.add(if_statement!!)
            BODY(body)

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
                body.variables.add(Variable(name, value))
                BODY(body)
            }
            else{
                if(tokens[index].symbol != "END") {
                    ok = false
                }
            }
        }
        else if (tokens[index].symbol == "action"){
            index++
            val actions=ACTION()
            body.action.add(actions)
            BODY(body)
        }
        return body
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
            val operator=tokens[index].lexem

            index++
            var operationsValue = OPERATIONS()
            if (operationsValue is Number){
                if(operator=="+"){
                    return value + operationsValue.toDouble()
                }
                else if(operator=="-") {
                    return value - operationsValue.toDouble()
                }
                else if(operator=="/") {
                    return value / operationsValue.toDouble()
                }
                else if(operator=="*") {
                    return value * operationsValue.toDouble()
                }
                else{
                    ok = false

                }

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

    fun CONDITION(): Condition?{

        var cond1=Variable("Condition1",OPERATOR())
        if(tokens[index].symbol == "checker"){
            val checker=tokens[index].lexem
            index++
            var cond2=Variable("Condition2",OPERATOR())
            val condition=Condition("Condition",cond1,checker,cond2)
            return condition
        }
        return null


    }

    fun RESERVED_IF():If?{
        if(tokens[index].lexem == "("){
            index++
            val condition=CONDITION()
            if(tokens[index].lexem == ")"){
                index++
                if(tokens[index].lexem == "{"){
                    index++
                    var b = Body()
                    val body=BODY(b)
                    if(tokens[index].lexem == "}"){
                        index++
                        val ifstatemnt=If("Ifstatement",condition!!,body)
                        return ifstatemnt

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
        return null
    }

    fun RESERVED_FOR():For?{
        val for_condition=FOR_CONDITION()
        if(tokens[index].lexem == "{"){
            index++
            var b = Body()
            val body=BODY(b)
            if(tokens[index].lexem == "}"){
                index++
                val for_statement=For("For",for_condition!!,body)
                return for_statement
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
        return null
    }

    fun FOR_CONDITION(): For_condition?{

        if(tokens[index].lexem == "(") {
            index++
            val condition1=CONDITION()
            if (tokens[index].lexem == "(") {
                index++
                if (tokens[index].symbol == "variable") {
                    val name=tokens[index].lexem
                    index++
                    if(tokens[index].lexem == "=") {
                        index++
                        val value=OPERATIONS()
                        val i=Variable(name, value)
                        variables.add(i)
                        if (tokens[index].lexem == ")") {
                            index++
                            if (tokens[index].lexem == ")") {
                                index++
                                val for_condition=For_condition("For",condition1!!,i)
                                return for_condition
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
        return null
    }

    fun ACTION():Action{
        val actions:Action=Action()
        if (tokens[index].lexem == "BuyItem"){

            index++
            if(tokens[index].lexem == "("){
                index++
                var list=BUY_ITEM_ARGS()
                if(tokens[index].lexem == ")"){
                    index++
                    var action_buylitem=BuyItem("BuyItem",list[0],list[1],list[2])
                    actions.BuyItems.add(action_buylitem)

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

                var list=SELL_ITEM_ARGS()
                if(tokens[index].lexem == ")"){
                    index++

                    var action_sellitem=SellItem("SellItem",list[0],list[1])
                    actions.SellItems.add(action_sellitem)



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
                var list = TRANSFER_MONEY_ARGS()
                if(tokens[index].lexem == ")"){
                    index++
                    var action_transfermoney=TransferMoney("TransferMoney", list[0], list[1], list[2])
                    actions.TransferMoney.add(action_transfermoney)
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
                var list=TRANSFER_ITEM_ARGS()
                if(tokens[index].lexem == ")"){
                    index++
                    var action_transferitem=TransferItem("TransferItem", list[0], list[1], list[2])
                    actions.TransferItem.add(action_transferitem)
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
        return actions

    }

    fun BUY_ITEM_ARGS(): MutableList<Variable>{
        var list: MutableList<Variable> = mutableListOf<Variable>();
        if(tokens[index].symbol == "variable"){
            var player=Variable("Player",tokens[index].lexem)
            index++
            if (tokens[index].lexem == ","){
                index++
                var price=Variable("Price",OPERATOR())
                if (tokens[index].lexem == ","){
                    index++
                    if(tokens[index].symbol == "variable"){
                        var item=Variable("Item",tokens[index].lexem)
                        index++
                        list.add(player)
                        list.add(price)
                        list.add(item)

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
        return list
    }

    fun SELL_ITEM_ARGS(): MutableList<Variable> {
        var list: MutableList<Variable> = mutableListOf<Variable>();
        if (tokens[index].symbol == "variable") {

            var player=Variable("Player",tokens[index].lexem)
            index++
            if (tokens[index].lexem == ",") {
                index++
                if (tokens[index].symbol == "variable") {

                    var item=Variable("Item",tokens[index].lexem)
                    index++

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

    fun TRANSFER_MONEY_ARGS(): MutableList<Variable>{
        var list: MutableList<Variable> = mutableListOf<Variable>();
        if(tokens[index].symbol == "variable"){
            var player1 = Variable("PlayerSender",tokens[index].lexem)
            index++
            if (tokens[index].lexem == ","){
                index++
                if (tokens[index].symbol == "variable"){
                    var player2 = Variable("PlayerReceiver", tokens[index].lexem)
                    index++
                    if(tokens[index].lexem == ","){
                        index++
                        var money = Variable("Money", OPERATOR())

                        list.add(player1)
                        list.add(player2)
                        list.add(money)


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
        return list
    }

    fun TRANSFER_ITEM_ARGS(): MutableList<Variable>{
        var list: MutableList<Variable> = mutableListOf<Variable>();
        if(tokens[index].symbol == "variable"){
            var player1 = Variable("PlayerSender", tokens[index].lexem)
            index++
            if (tokens[index].lexem == ","){
                index++
                if (tokens[index].symbol == "variable"){
                    var player2 = Variable("PlayerReceiver", tokens[index].lexem)
                    index++
                    if(tokens[index].lexem == ","){
                        index++
                        if(tokens[index].symbol == "variable"){
                            var item = Variable("Item", tokens[index].lexem)
                            index++

                            list.add(player1)
                            list.add(player2)
                            list.add(item)


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
        return list
    }
}

class TransferMoney(val name:String, val player1:Variable, val player2:Variable, val money:Variable,val index:Int){
    fun tostring():String{
        return "TransferMoney"+ "( "+player1.value+", "+player2.value+", "+ money.value+" )"

    }
    fun execute():String{
        return "Executing action TransferMoney from "+ player1.value +" to "+player2.value+" for value of "+money.value
    }
}
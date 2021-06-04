class TransferMoney(val name:String, val player1:Variable, val player2:Variable, val money:Variable,val index:Int){
    fun tostring():String{
        return "TransferMoney"+ "( "+player1.value+", "+player2.value+", "+ money.value+" )"

    }
}
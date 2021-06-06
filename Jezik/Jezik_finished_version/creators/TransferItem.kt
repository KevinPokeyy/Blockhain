class TransferItem(val name:String, val player1:Variable, val player2:Variable, val item:Variable,val index:Int){
    fun tostring():String{
        return "TransferItem"+ "( "+player1.value+", "+player2.value+", "+ item.value+" )"

    }
    fun execute():String{
        return "Executing action TransferItem from "+ player1.value +" to "+player2.value+" for item "+item.value
    }
}
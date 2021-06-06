class SellItem(val name:String,val player:Variable,val item:Variable,val index:Int) {
    fun tostring():String{
        return "SellItem"+ "("+player.value+", "+ item.value+")"

    }
    fun execute():String{
        return "Executing action SellItem "+ player.value +" sells item "+item.value
    }
}
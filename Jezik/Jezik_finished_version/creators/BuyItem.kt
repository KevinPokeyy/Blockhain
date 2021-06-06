class BuyItem(val name:String,val player:Variable,val price:Variable,val item:Variable,val index:Int) {
    fun tostring():String{
        return "BuyItem"+ "( "+player.value+", "+price.value+", "+ item.value+" )"

    }
    fun execute():String{
        return "Executing action BuyItem "+ player.value +" buys item "+item.value+" for value of "+price.value
    }

}
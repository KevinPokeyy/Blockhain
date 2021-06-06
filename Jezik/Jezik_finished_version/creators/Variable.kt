class Variable (val name: String, val value: Any, val index:Int) {
    fun tostring():String{
        return name+" = "+value.toString()+ " //Variable"
    }
    fun execute():String{
        return "Initializing variable "+name+" with value of "+ value
    }
}
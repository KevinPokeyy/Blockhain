class Variable (val name: String, val value: Any, val index:Int) {
    fun tostring():String{
        return name+" = "+value.toString()
    }
}
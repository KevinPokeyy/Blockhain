class For_condition(val name:String,val condition:Condition,val i: Variable,val index:Int){
    fun tostring():String{
        return condition.tostring()+", "+i.name+"++"
    }
}
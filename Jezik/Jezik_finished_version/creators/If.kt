class If(val name:String, val condition:Condition, val body:Body, var index:Int) {
    fun tostring():String{
        return "If ( "+condition.tostring()+" )"+body.tostring()
    }

    fun execute():String{
       return "Executing body if condition "+condition.tostring()+" is met\n"+body.tostring()

    }
}
class For(val name:String,val forCondition: For_condition,val body:Body,val index:Int) {
    fun tostring():String{
        return "For ( "+forCondition.tostring()+" )"+ body.tostring()
    }
}
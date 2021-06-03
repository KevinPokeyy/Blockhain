class Condition(val name:String,val condition1:Variable,val operand:String,val condition2:Variable,val index:Int){

fun tostring():String{
    return condition1.name+" "+operand+" "+ condition2.value


}
}
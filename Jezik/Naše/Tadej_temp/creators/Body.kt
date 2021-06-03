class Body(val index:Int) {
    val ifstatements: MutableList<If> = mutableListOf<If>()
    val forstatements: MutableList<For> = mutableListOf<For>();
    val action: MutableList<Action> = mutableListOf<Action>();
    val variables: MutableList<Variable> = mutableListOf<Variable>();






    fun findvar():Variable{
        var current=variables[0].index
        var ind:Int=0
        for(i in variables.indices){
            if(variables[i].index <current){
                current=variables[i].index
                ind=i
            }

        }
        return variables[ind]
    }
    fun findif():If{
        var current=ifstatements[0].index
        var ind:Int=0
        for(i in ifstatements.indices){
            if(ifstatements[i].index <current){
                current=ifstatements[i].index
                ind=i
            }
        }
        return ifstatements[ind]
    }
    fun findfor():For{
        var current=forstatements[0].index
        var ind:Int=0
        for(i in forstatements.indices){
            if(forstatements[i].index <current){
                current=forstatements[i].index
                ind=i
            }

        }
        return forstatements[ind]
    }


    fun findaction():Action{

        var current=action[0].findact()
        var ind:Int=0
        for(i in action.indices){
            if(action[i].findact() <current){
                current=action[i].findact()
                ind=i
            }


        }
        return action[ind]

    }





    fun tostring():String{

        var out:String="{"

        while(variables.isEmpty()==false){
            var i1:Variable
            var i2:Action
            var i3:For
            var i4:If

            if(variables.isEmpty()==false) i1=findvar()
            if(action.isEmpty()==false) i2=findaction()
            if(forstatements.isEmpty()==false) i3=findfor()
            if(ifstatements.isEmpty()==false) i4=findif()



            if(i1.index<i2.index){
                if(i1.index<i3.index){
                    if(i1.index<i4.index){
                        out+="\n"+ i1.toString()
                        variables.remove(i1)

                    }
                }

            }
            else if(i2.index<i1.index){
                if(i2.index<i3.index){
                    if(i2.index<i4.index){
                        out+="\n"+ i2.toString()
                        action.remove(i2)

                    }

                }

            }
            else if(i3.index<i1.index){
                if(i3.index<i2.index){
                    if(i3.index<i4.index){
                        out+="\n"+ i3.toString()
                        forstatements.remove(i3)

                    }

                }

            }
            else if(i4.index<i1.index){
                if(i4.index<i2.index){
                    if(i4.index<i3.index){
                        out+="\n"+ i4.toString()
                        ifstatements.remove(i4)

                    }

                }

            }



        }








        return out+"\n}"


    }


}
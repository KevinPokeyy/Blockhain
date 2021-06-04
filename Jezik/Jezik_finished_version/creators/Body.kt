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

        var current=action[0].index
        var ind:Int=0
        for(i in action.indices){
            if(action[i].index <current){
                current=action[i].index
                ind=i
            }


        }
        return action[ind]

    }





    fun tostring():String{

        var out:String="{"



        while(variables.isEmpty()==false||ifstatements.isEmpty()==false ||forstatements.isEmpty()==false||action.isEmpty()==false ){
            val list: MutableList<Int> = mutableListOf<Int>()
            lateinit  var i1: Variable
            lateinit  var i2: If
            lateinit  var i3: For
            lateinit  var i4: Action

            if(variables.isEmpty()==false){
                i1=findvar()
                list.add(i1.index)

            }
            if(ifstatements.isEmpty()==false){
                i2=findif()
                list.add(i2.index)

            }
            if(forstatements.isEmpty()==false){
                i3=findfor()
                list.add(i3.index)

            }
            if(action.isEmpty()==false){
                i4=findaction()
                list.add(i4.index)

            }
            if(list.isEmpty()==false){
                var current=list[0]
                for(i in list.indices){
                    if(list[i]<current){
                        current=list[i]
                    }
                }
                if(variables.isEmpty()==false){
                    if(current==i1.index){
                        out+="\n"+i1.tostring()
                        variables.remove(i1)

                    }

                }
                if(ifstatements.isEmpty()==false){
                    if(current==i2.index){
                        out+="\n"+i2.tostring()
                        ifstatements.remove(i2)

                    }

                }
                if(forstatements.isEmpty()==false){
                    if(current==i3.index){
                        out+="\n"+i3.tostring()
                        forstatements.remove(i3)

                    }

                }
                if(action.isEmpty()==false){
                    if(current==i4.index){
                        out+="\n"+i4.tostring()
                        action.remove(i4)

                    }

                }

            }



        }




        return out+"\n}"


    }


}
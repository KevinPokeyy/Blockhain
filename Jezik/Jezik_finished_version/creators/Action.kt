class Action(val index:Int) {
    val BuyItems: MutableList<BuyItem> = mutableListOf<BuyItem>();
    val SellItems: MutableList<SellItem> = mutableListOf<SellItem>();
    val TransferMoney: MutableList<TransferMoney> = mutableListOf<TransferMoney>();
    val TransferItem: MutableList<TransferItem> = mutableListOf<TransferItem>();

    fun tostring():String{
        var output:String=""
        for(i in BuyItems){
           output+= "\n"+i.tostring()
        }
        for(i in SellItems){
            output+="\n"+i.tostring()
        }
        for(i in TransferMoney){
            output+="\n"+i.tostring()
        }
        for(i in TransferItem){
            output+="\n"+i.tostring()
        }
        return output


    }
    fun findbuy():BuyItem{
        var current=BuyItems[0].index
        var ind:Int=0
        for(j in BuyItems.indices){
            if(BuyItems[j].index  >current){
                current=BuyItems[j].index
                ind=j
            }

        }
        return BuyItems[ind]

    }
    fun findsell():SellItem{
        var current=SellItems[0].index
        var ind:Int=0
        for(j in SellItems.indices){
            if(SellItems[j].index  >current){
                current=SellItems[j].index
                ind=j
            }

        }
        return SellItems[ind]

    }
    fun findtransferm():TransferMoney{
        var current=TransferMoney[0].index
        var ind:Int=0
        for(j in TransferMoney.indices){
            if(TransferMoney[j].index  >current){
                current=TransferMoney[j].index
                ind=j
            }

        }
        return TransferMoney[ind]

    }
    fun findtransferi():TransferItem{
        var current=TransferItem[0].index
        var ind:Int=0
        for(j in TransferItem.indices){
            if(TransferItem[j].index  >current){
                current=TransferItem[j].index
                ind=j
            }

        }
        return TransferItem[ind]

    }
    fun findact():Int{

        val i1=findbuy()
        val i2=findsell()
        val i3=findtransferm()
        val i4=findtransferi()
        var out:Int=0


        if(i1.index<i2.index){
            if(i1.index<i3.index){
                if(i1.index<i4.index){
                    out= i1.index

                }
            }

        }
        else if(i2.index<i1.index){
            if(i2.index<i3.index){
                if(i2.index<i4.index){
                    out= i2.index

                }

            }

        }
        else if(i3.index<i1.index){
            if(i3.index<i2.index){
                if(i3.index<i4.index){
                    out= i3.index

                }

            }

        }
        else if(i4.index<i1.index){
            if(i4.index<i2.index){
                if(i4.index<i3.index){
                    out= i4.index

                }

            }

        }
        return out


    }






}
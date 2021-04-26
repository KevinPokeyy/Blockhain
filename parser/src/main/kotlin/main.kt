import ch.qos.logback.classic.spi.CallerData.extract
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.*
import it.skrape.*
import it.skrape.matchers.toBe
import it.skrape.matchers.toContain
import it.skrape.selects.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.Exception

data class MyDataClass(
    var httpStatusCode: Int = 0,
    var httpStatusMessage: String = "",
    var paragraph: String = "",
    var allParagraphs: List<String> = emptyList(),
    var allLinks: List<String> = emptyList()
)

class Weapon (val name: String, val damagePerAttack: Number, val damagePerSecond: Number, val attacksPerSecond: Number,
                val bonus: Number, val criticalChanceMultiplier: Number, val criticalDamage: Number, val actionPointCost: Number,
                    val damagePerActionPoint: Number, val weaponSpread: Number, val magazineCapacity: Number,
                        val durability: Number, val weight: Number, val valueInCaps: Number,
                          val valueToWeightRation: Number, val skillRequired: Number, val strengthRequired: Number){}

@Serializable
class EazyWeapon(val name: String, val stats: ArrayList<String>){}

class HtmlExtractionService {

    fun extract() : List<String> {
        val extracted = skrape(HttpFetcher) {
            request {
                url = "https://fallout.fandom.com/wiki/Fallout:_New_Vegas_weapons"
            }

            extractIt<MyDataClass> {
                htmlDocument {
                    it.allParagraphs = table {
                        withClass = "va-table"
                        findAll {
                            td {
                                findAll { a { findAll { eachHref } } }
                            }
                        }
                    }
                }
            }
        }
        return extracted.allParagraphs
    }

    fun extract2(path : String) : List<String> {
        try {
            val extracted = skrape(HttpFetcher) {
                request {
                    url = "https://fallout.fandom.com/$path"
                }

                extractIt<MyDataClass> {
                    htmlDocument {
                        it.allParagraphs = table {
                            withClass = "va-table-weapon-comparison"
                            findFirst {
                                tr {
                                    findSecond {
                                        td {
                                            findAll {
                                                eachText
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
            return extracted.allParagraphs
        }
        catch(e: Exception) {

        }
        return arrayListOf()
    }

}

fun main(){
    val hello = HtmlExtractionService()
    val helper = hello.extract()

    for (i in helper) {
        println(i)
    }

    var everything : ArrayList<String> = ArrayList()
    var counter = 0
    var prev = ""
    for (i in helper) {
        if(counter > 2) {
            if(i != prev){
                var tmp : String = hello.extract2(i).toString()
                println(tmp)
                if(!everything.contains(tmp)) {
                    everything.add(tmp)
                    println("ADDED")
                }

            }
        }
        counter++
        prev = i
    }

    var eazyWeaponContainer = arrayListOf<EazyWeapon>()
    for(i in everything){
        i.replace("[", "")
        i.replace("]","")
        val parts = i.split(",")
        var name: String = ""
        var stats = arrayListOf<String>()
        counter = 0
        for(i in parts) {
            counter++
            if(counter == 1){
                name = i
            }
            else {
                stats.add(i)
            }
            print("$i + ")

        }
        eazyWeaponContainer.add(EazyWeapon(name, stats))
        print("\n")
    }

    println("\n\n")

    for(i in eazyWeaponContainer){
        println("${i.name} ${i.stats}")
    }

    println("\n\n\n${Json.encodeToString(eazyWeaponContainer[0])}")







}

//<table class="va-table full center bottom mw-collapsible mw-made-collapsible" style="width:50%; margin:auto">

//withClass = "foo"
//findFirst {
//    text toBe "some p-element"
//    className  toBe "foo"
//}


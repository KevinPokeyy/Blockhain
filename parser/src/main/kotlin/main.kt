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
import java.io.File
import java.lang.Exception

data class MyDataClass(
    var httpStatusCode: Int = 0,
    var httpStatusMessage: String = "",
    var paragraph: String = "",
    var allParagraphs: List<String> = emptyList(),
    var allLinks: List<String> = emptyList()
)
@Serializable
class Weapon (val name: String, val damage: Float,
              val damagePerSecond: Float, val weight: Float, val cost: Float, val skillRequired: Float){}

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
        catch(e: Exception) {}
        return arrayListOf()
    }

}

fun main(){
    val hello = HtmlExtractionService()
    val helper = hello.extract()

    var helper2 = arrayListOf<String>()
    for (i in helper) {

        if(!helper2.contains(i)) {
            helper2.add(i)
            println(i);
        }
    }
    println()

    var everything : ArrayList<String> = ArrayList()
    var counter = 0
    var prev = ""
    for (i in helper2) {
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

    var weaponContainer = arrayListOf<Weapon>()
    var j = 0
    for(i in everything) {
        everything[j] = everything[j].replace("[", "")
        everything[j] = everything[j].replace("]", "")
        val parts = everything[j].split(",")
        var name: String = ""
        var stats = arrayListOf<String>()
        counter = 0
        j++
        try {
            val newWeapon: Weapon = Weapon(
                parts[0], parts[1].toFloat(), parts[2].toFloat(),
                parts[parts.size - 5].toFloat(), parts[parts.size - 4].toFloat(), parts[parts.size - 1].toFloat()
            )
            weaponContainer.add(newWeapon)
        } catch (e: Exception) {
        }

    }

    println("\n\n")

    for(i in weaponContainer){
        println("${i.name}, ${i.damage}, ${i.damagePerSecond}, ${i.weight}, ${i.cost}, ${i.skillRequired}")
    }

    println("\n\n\n${Json.encodeToString(weaponContainer[0])}")
    val file = "weapons.json"
    File(file).writeText(Json.encodeToString(weaponContainer))





}


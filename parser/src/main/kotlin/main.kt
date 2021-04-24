import ch.qos.logback.classic.spi.CallerData.extract
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.and
import it.skrape.selects.eachImage
import it.skrape.selects.eachText
import it.skrape.selects.eachHref
import it.skrape.selects.html5.a
import it.skrape.selects.html5.div
import it.skrape.selects.html5.p
import it.skrape.selects.html5.span

data class MyDataClass(
    var httpStatusCode: Int = 0,
    var httpStatusMessage: String = "",
    var paragraph: String = "",
    var allParagraphs: List<String> = emptyList(),
    var allLinks: List<String> = emptyList()
)

class HtmlExtractionService {

    fun extract() {
        val extracted = skrape(HttpFetcher) {
            request {
                url = "http://localhost:80"
            }

            extractIt<MyDataClass> {
                htmlDocument {
                    it.allParagraphs = p { findAll { eachText }}
                    it.paragraph = p { findFirst { text }}
                    it.allLinks = a { findAll { eachHref }}
                }
            }
        }
        for(i in extracted.allParagraphs)
            println(i)
        // will print:
        // MyDataClass(httpStatusCode=200, httpStatusMessage=OK, paragraph=i'm a paragraph, allParagraphs=[i'm a paragraph, i'm a second paragraph], allLinks=[http://some.url, http://some-other.url])
    }
}

fun main(){
    val hello = HtmlExtractionService()
    hello.extract()
}
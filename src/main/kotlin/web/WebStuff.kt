package web

import java.awt.Desktop
import java.lang.Thread.sleep
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val SEARCH_RESULTS_AWAITING_TIME = 10000

// packed search results
class SearchResults(
    val headers: List<String>,
    val pageIDs: List<String>
) {
    init {
        require(headers.count()==pageIDs.count())
    }

    // opens page in browser
    // index must be in (0..count-1) range
    fun openInBrowser(index: Int) {
        Desktop.getDesktop().browse(URI.create("https://ru.wikipedia.org/w/index.php?curid=${pageIDs[index]}"))
    }

}

// sends http request and returns results (null if something goes wrong, e.g. no connection)
// request = your normal search bar request string
fun sendWikiSearchRequest(request: String) : SearchResults? {
    val client = HttpClient.newBuilder().build()
    val webRequest = HttpRequest.newBuilder()
        .uri(URI.create("https://ru.wikipedia.org/w/api.php?action=query&list=search&utf8=&format=json&srsearch=${URLEncoder.encode(request,"UTF-8")}"))
        .build()

    val promise = client.sendAsync(webRequest, HttpResponse.BodyHandlers.ofString())

    for (timer in 0..SEARCH_RESULTS_AWAITING_TIME step 50) {    // check every 0.05 sec. Its magic const, but lets leave it be, its not essential
        sleep(50)
        if (promise.isDone)
            break
    }

    if (!promise.isDone) {
        print("It seems searching takes too long. Do you want to continue waiting? [Y/n] ")
        val ans = readln()
        if (ans!="y" && ans!="Y") {
            promise.cancel(true)
            return null
        }
    }

    val webResponse = promise.get() as HttpResponse<String>

    if (webResponse.statusCode()!=200)      // idk if there are pre-defined http code consts, 200 is for OK
        return null

    // how do i tell intellij to stfu and stop highlighting pageid?
    val pageRegex = Regex("\"title\":\"([^\"]+)\",\"pageid\":(\\d+)")
    val results = pageRegex.findAll(webResponse.body())

    val headers = ArrayList<String>()
    val links = ArrayList<String>()

    for (res in results) {
        headers.add(res.groupValues[1])
        links.add(res.groupValues[2])
    }

    // using mutable lists kinda breaks encapsulation, but... who cares?
    // its not enterprise development
    return SearchResults(headers, links)
}


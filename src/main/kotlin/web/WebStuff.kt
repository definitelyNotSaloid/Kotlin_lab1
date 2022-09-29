package web

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

// packed search results
class SearchResults(
    val headers: List<String>,
    val links: List<String>
) {
    init {
        require(headers.count()==links.count())
    }

    // opens page in browser
    // index must be in (0..count-1) range
    fun openInBrowser(index: Int) {
        TODO()
    }

}

// sends http request and returns results (null if something goes wrong, e.g. no connection)
// request = your normal search bar request string
fun sendWikiSearchRequest(request: String) : SearchResults? {
    val client = HttpClient.newBuilder().build()
    val webRequest = HttpRequest.newBuilder()
        .uri(URI.create("https://ru.wikipedia.org/w/api.php?action=query&list=search&utf8=&format=json&srsearch=${URLEncoder.encode(request,"UTF-8")}"))
        .build()

    val webResponse = client.send(webRequest, HttpResponse.BodyHandlers.ofString())

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


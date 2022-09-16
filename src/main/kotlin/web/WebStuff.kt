package web

// packed search results
class SearchResults(
    headers: List<String>,
    links: List<String>
) {
    init {
        require(headers.count()==links.count())
    }

    // amount of search results
    val count : Int = headers.count()

    // returns header of i'th page
    // index must be in (0..count-1) range
    fun header(index: Int) {

    }

    // opens page in browser
    // index must be in (0..count-1) range
    fun openInBrowser(index: Int) {

    }

}

// sends http request and returns results (null if something goes wrong, e.g. no connection)
// request = your normal search bar request string
fun sendWikiSearchRequest(request: String) : SearchResults? {
    TODO()
}


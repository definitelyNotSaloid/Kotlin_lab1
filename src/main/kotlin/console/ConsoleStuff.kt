package console

import web.SearchResults

// basically a controller+view
// stores responses and whatever other stuff we need
class ConsoleHandler {
    private var results : SearchResults? = null
    fun readSearchRequest() {
        results = web.sendWikiSearchRequest(readln())
        // TODO null checks
    }

    fun readAndOpenPageInBrowser() {
        val pageIndex = readln().toIntOrNull()
        require(pageIndex != null)
        results!!.openInBrowser(pageIndex)
        results = null  // should we save our results after the page has been opened in browser?
    }

    fun printSearchResults() {
        TODO()
    }
}
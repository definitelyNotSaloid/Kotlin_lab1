import console.ConsoleHandler

fun main(args: Array<String>) {
    val consoleHandler = ConsoleHandler()
    // add a loop?
    consoleHandler.readSearchRequest()
    consoleHandler.printSearchResults()
    consoleHandler.readAndOpenPageInBrowser()
}
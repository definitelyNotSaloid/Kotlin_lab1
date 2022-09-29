import console.ConsoleHandler

fun main(args: Array<String>) {
    val consoleHandler = ConsoleHandler()
    println("So... what would you like to search for today?\n" +
            "Please tell me! Im much more useful than that stupid search bar!\n")
    while (true) {
        consoleHandler.initSearch()
        // sure, we can make an escape loop command, but using that funny red cross at top right is simpler
    }
}
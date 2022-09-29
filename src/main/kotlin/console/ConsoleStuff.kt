package console

import web.SearchResults
import java.lang.Thread.sleep

// basically a controller+view
// stores responses and whatever other stuff we need
class ConsoleHandler {
    private var results : SearchResults? = null
    fun initSearch() {
        print("Enter your search request right here ----> ")
        results = web.sendWikiSearchRequest(readln())
        if (results==null)
            println(
                "Invalid response from server or no response at all.\n" +
                        "How about checking internet connection?\n" +
                        "Or try again. It will work this time, i promise!\n\n"
            )
        else if (results!!.pageIDs.isEmpty())
            println("Nothing found, or so it seems...\n" +
                    "Ask your parents about it!\n" +
                    "Or try and search for something wikipedia knows about\n\n")

        else {
            val resultsCount = results!!.headers.count()
            println("Look! Heres what i found!\n\n")

            for (i in 1..resultsCount)
                println("$i. ${results!!.headers[i-1]}")

            println("0. I dont like these results! I want to search for something else!\n")

            print("Make your choice (0-$resultsCount): ")
            var choice = readln().toIntOrNull()

            if (choice == null) {
                println("Congrats on failing a simple task of entering an integer! Try again. And try not to fail this time")
                choice = readln().toIntOrNull()
                if (choice == null) {
                    println("You want to just see me making an extent comment about your miserable skills of following simple instructions, am i right?\n" +
                            "Im sorry to disappoint you, my \"special\" friend, i shall not humiliate you any further.\n" +
                            "You know, ill just open the first page for you. No thanks needed, anything for my disabled buddy!\n")
                    results!!.openInBrowser(0)
                    return
                }
            }

            if (choice !in 0..resultsCount) {
                print("Look, i know math can be hard, especially comparing integers.\n" +
                        "But try your best and enter a number that is NOT negative (it has no minus (this ---> - <---- thing) before digits)\n" +
                        "and is EQUAL OR LESS then $resultsCount. Its when a number goes before other. \n" +
                        "Like, if cookie's price is equal or less then your pocket money, then you can afford them.\n" +
                        "So... DO YOUR BEST NOW: ")
                choice = readln().toIntOrNull()
                if (choice==null) {
                    println("Now this is not a number at all... Are you like Bobby who cant enter a bunch of digits?\n" +
                            "Or are you just trying to enter something like \"first one pls\"?\n" +
                            "Whatever the case... Ill just open 1st page. I mean, who opens something besides first page, am i right?\n")
                    results!!.openInBrowser(0)
                    return
                }
                if (choice !in 0..resultsCount) {
                    println("Look, i can understand that guy who cant enter a number. He is a special one. But you can!\n" +
                            "So what is your problem? You could have just entered 0 (this funny circle is called zero btw) and be good, BUT NOOOOO.\n" +
                            "Stay here for a while. Think about your life choices that led you to being unable to enter a number in given range.\n")
                    sleep(1000000000)
                    return
                }
            }

            //
            while (choice in 1..resultsCount) {
                println("Opening...")
                results!!.openInBrowser(choice!! - 1)
                print("Enter another page index or 0 to exit: ")
                choice = readln().toIntOrNull()
            }

            if (choice==0)
                println("If you say so...")
            else
                println("Invalid page. Returning to search input")

            return
        }
    }
}
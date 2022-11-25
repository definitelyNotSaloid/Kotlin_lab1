import lab2.CsvHouseParser
import lab2.HouseData
import lab2.Parser
import lab2.bicycles.*
import java.util.*
import kotlin.collections.HashMap
import kotlinx.coroutines.*
import lab2.XmlHouseParser
import kotlin.collections.HashSet

private fun readYesOrNo() : Boolean {
    val response = readln().lowercase(Locale.getDefault())
    return (response=="yes" || response=="y")
}

private suspend fun parseFile(
    parser: Parser,
    housesWithNFloors : MutableList<Int>,
    duplicatesList : HashMap<HouseData, GenericBox<Int>>) : Unit = coroutineScope {
    async {
        val parsedHouses = HashSet<HouseData>()
        var i = 0
        parser.getAllLazy().forEach { house ->

            if (house.floors in 0..6)
                housesWithNFloors[house.floors]++

            val houseHash = house.hashCode()
            if (parsedHouses.contains(house)) {

                val duplicateCount =
                    duplicatesList.getOrPut(house) {
                        GenericBox(1)                       // we start with 1 because if we found house hash in list
                        // than it means there already was 1 entry.
                    }
                duplicateCount.value++
            } else {
                parsedHouses.add(house)
            }

            if (i%10000 == 0)
                println("Parsed $i lines and found ${duplicatesList.count()} duplicates in total")

            i++
        }
    }
}.await()

fun main(args: Array<String>) {

    println("Im reading cvs file by default. Do you want to parse xml instead? [Y/n]")
    val parser : Parser = if (readYesOrNo())
        XmlHouseParser("address.xml")
    else
        CsvHouseParser("address.csv")

    val housesWithNFloors = arrayListOf(0,0,0,0,0,0)    // 1st value is "dummy". its easier when index 1 stands for 1st floor

    val duplicatesList = HashMap<HouseData, GenericBox<Int>>()

    runBlocking {
        launch {
            parseFile(parser, housesWithNFloors, duplicatesList)
        }
    }


    println("Total houses with")
    for (i in 1..5)
        println("$i floors: ${housesWithNFloors[i]}")

    println()
    println("There are ${duplicatesList.count()} duplicated entries")
    println("Show them? [Y/n]")
    if (readYesOrNo()) {
        duplicatesList.forEach{
            println(it.key.prettyString())
            println("Entries: ${it.value.value}")
            println("__________")
        }
    }
}
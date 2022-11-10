import lab2.CsvHouseParser
import lab2.HouseData
import lab2.Parser
import lab2.bicycles.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.*
import java.lang.Thread.sleep
import kotlin.collections.HashSet

private suspend fun parseFile(
    parser: Parser,
    housesWithNFloors : MutableList<Int>,
    duplicatesList : HashMap<HouseData, GenericBox<Int>>) : Unit = coroutineScope {
    async {
        val hashesOfParsedHouses = HashSet<Int>()

        var i = 0
        parser.getAllLazy().forEach { house ->

            if (house.floors in 0..6)
                housesWithNFloors[house.floors]++

            val houseHash = house.hashCode()
            if (hashesOfParsedHouses.contains(houseHash)) {
                val duplicateCount =
                    duplicatesList.getOrPut(house) {
                        GenericBox(1)                       // we start with 1 because if we found house hash in list
                        // than it means there already was 1 entry.
                    }
                duplicateCount.value++
            } else {
                hashesOfParsedHouses.add(houseHash)
            }

            if (i%10000 == 0)
                println("Parsed $i lines and found ${duplicatesList.count()} duplicates in total")

            i++
        }
    }
}.await()

fun main(args: Array<String>) {
    val parser : Parser
    parser = CsvHouseParser("address.csv")
    val housesWithNFloors = arrayListOf(0,0,0,0,0,0)    // 1st value is "dummy". its easier when index 1 stands for 1 floor

    val duplicatesList = HashMap<HouseData, GenericBox<Int>>()

    runBlocking {
        val job = launch {
            parseFile(parser, housesWithNFloors, duplicatesList)
        }
    }


    println("Total houses with")
    for (i in 1..5)
        println("$i floors: ${housesWithNFloors[i]}")

    println()
    println("There are ${duplicatesList.count()} duplicated entries")
    println("Show them? [Y/n]")
    val response = readln().lowercase(Locale.getDefault())
    if (response=="yes" || response=="y") {
        duplicatesList.forEach{
            println(it.key.prettyString())
            println("Entries: ${it.value.value}")
            println("__________")
        }
    }
}
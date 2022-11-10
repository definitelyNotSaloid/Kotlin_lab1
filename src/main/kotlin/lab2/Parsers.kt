package lab2

import java.io.File
import java.lang.Thread.yield



data class HouseData constructor(
    val city : String,
    val street : String,
    val house : Int,
    val floors: Int) {

    fun prettyString() : String {
        return "$street, $house,\n$city\nTotal floors: $floors\n"
    }
}



abstract class Parser(protected val filePath: String) {
    abstract fun getAllLazy() : Sequence<HouseData>
}

class XmlHouseParser(filePath : String) : Parser(filePath) {
    override fun getAllLazy() : Sequence<HouseData> {
        TODO()
    }
}

class CsvHouseParser(filePath: String) : Parser(filePath) {
    private val houseDataRegex : Regex = Regex("\"([^\"]+)\";\"([^\"]+)\";(\\d+);(\\d+)")     // "city";"street";house_int;floors_int

    override fun getAllLazy() : Sequence<HouseData> {
        return File(filePath).bufferedReader().lineSequence()
            .map {line ->
                val match = houseDataRegex.find(line)
                if (match==null)
                    null
                else {
                    val ctorArgs = match.groupValues.drop(1).toList()

                    // assuming regex is correct, .toInt() wont throw any exceptions
                    HouseData(ctorArgs[0], ctorArgs[1], ctorArgs[2].toInt(), ctorArgs[3].toInt())
                }
            }.filterNotNull()
    }
}
package lab2

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import java.io.File


@Suppress("ConvertSecondaryConstructorToPrimary")
data class HouseData (
    @field:JacksonXmlProperty(isAttribute = true) var city : String = "",
    @field:JacksonXmlProperty(isAttribute = true) var street : String = "",
    @field:JacksonXmlProperty(isAttribute = true) var house : Int = -1,
    @field:JacksonXmlProperty(isAttribute = true, localName = "floor") var floors: Int = -1) {

    constructor() : this("","",-1,-1) {

    }

    fun prettyString() : String {
        return "$street, $house,\n$city\nTotal floors: $floors\n"
    }
}



abstract class Parser(protected val filePath: String) {
    abstract fun getAllLazy() : Sequence<HouseData>
}

class XmlHouseParser(filePath : String) : Parser(filePath) {

    override fun getAllLazy() : Sequence<HouseData> {
        val xmlMapper = XmlMapper()
        return File(filePath).bufferedReader().lineSequence()
            .map {line ->
                try {
                    xmlMapper.readValue(line, HouseData::class.java)
                }
                catch (e: Exception) {
                    null
                }
            }.filterNotNull()
    }
}

class CsvHouseParser(filePath: String) : Parser(filePath) {
    private val houseDataRegex : Regex = Regex("\"([^\"]+)\";\"([^\"]+)\";(\\d+);(\\d+)")     // "city";"street";house_int;floors_int/

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
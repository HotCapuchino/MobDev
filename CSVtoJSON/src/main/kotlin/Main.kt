import com.google.gson.Gson
import krangl.DataFrame
import krangl.readCSV
import java.io.File

fun mapCSV2JSON(filename: String): String {
    val csvFile = DataFrame.readCSV(filename)
    val appsHashMap: HashMap<String, ArrayList<App>> = HashMap()
    val gson = Gson()
    for (row in csvFile.rows) {
        val genres: ArrayList<String> = ArrayList()
        for (genre in row["Genres"].toString().split(";")) {
            genres.add(genre)
        }

        val installs = row["Installs"].toString().replace(Regex("\\D"), "")
        val price = row["Price"].toString().replace(Regex("\\D"), "")

        val newApp = App(
            row["App"].toString(),
            if (row["Rating"].toString().toDouble().isNaN()) 0.0 else row["Rating"].toString().toDouble(),
            row["Reviews"].toString(),
            row["Size"].toString(),
            if (installs.isNotEmpty()) installs.toInt() else -1,
            row["Type"].toString(),
            if (price.isNotEmpty()) price.toInt() != 0 else false,
            row["Content Rating"].toString(),
            genres,
            row["Last Updated"].toString(),
            row["Current Ver"].toString(),
            mapVer(row["Android Ver"].toString().replace(Regex("[A-Za-z\\s]"), "")))

        if (!appsHashMap.keys.contains(row["Category"].toString())) {
            val newArrayMap: ArrayList<App> = ArrayList()
            newArrayMap.add(newApp)
            appsHashMap[row["Category"].toString()] = newArrayMap
        } else {
            appsHashMap[row["Category"].toString()]?.add(newApp)
        }
    }
    return gson.toJson(appsHashMap)
}

fun mapVer(ver: String): String {
    when (ver) {
        "2.3" -> return "9"
        "2.3.3" -> return "10"
        "3.0" -> return "11"
        "3.1" -> return "12"
        "3.2" -> return "13"
        "4.0" -> return "14"
        "4.0.3" -> return "15"
        "4.1" -> return "16"
        "4.2" -> return "17"
        "4.3" -> return "18"
        "4.4" -> return "19"
        "4.4W" -> return "20"
        "5.0" -> return "21"
        "5.1" -> return "22"
        "6.0" -> return "23"
        "7.0" -> return "24"
        "7.1" -> return "25"
        "8.0" -> return "26"
        "8.1" -> return "27"
        "9" -> return "28"
        "10" -> return "29"
        "11" -> return "30"
        else -> return "NAN"
    }
}

fun main() {
    val csvPath = "src/main/resources/googleplaystore.csv"
    val outJson = "src/main/out/googleplaystore.json"
    val jsonRepresentation = mapCSV2JSON(csvPath)
    File(outJson).bufferedWriter().write(jsonRepresentation)
}
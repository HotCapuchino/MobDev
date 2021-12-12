import java.io.BufferedReader
import java.io.File

fun main() {
    val rolesDict = mutableMapOf<String, HashMap<Int, String>>()
    val streamReader = BufferedReader({}.javaClass.getResource("roles.txt").openStream().buffered().reader())
    var readingRoles = false
    var counter = 1
    streamReader.forEachLine { line ->
        if (line.contains("roles")) {
            readingRoles = true
        } else if (line.contains("textLines")) {
            readingRoles = false
        } else {
            if (readingRoles) {
                rolesDict[line] = HashMap()
            } else {
                val role = line.split(':')[0]
                val roleText = line.substring(line.indexOf(':') + 1).trim()
                val rolesPhrases = rolesDict[role]
                rolesPhrases?.let { it -> it[counter] = roleText }
                counter++
            }
        }
    }
    File("./out/roleplay.txt").bufferedWriter().use { buffWriter ->
        for (role in rolesDict.keys) {
            buffWriter.write("${role}:\n")
            val rolePhrases = rolesDict[role]
            rolePhrases?.let { rolePhrases ->
                if (rolePhrases.keys.size == 0) {
                    buffWriter.write("---\n")
                } else {
                    for (phraseKey in rolePhrases.keys) {
                        buffWriter.write("${phraseKey}) ${rolePhrases[phraseKey]}\n")
                    }
                }
            }
            buffWriter.newLine()
        }
    }
}
package org.decembrist.common

import java.io.File
import javax.script.ScriptEngineManager
//import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmDaemonLocalEvalScriptEngineFactory as KScript

class Script(val text: String, val other: List<String>) {

    private val filtered: String

    init {
        val commonLines = getResourceFile(COMMON_KT).readLines()
        filtered = text.lines()
                .filter { !it.startsWith("import org.decembrist") }
                .filter { !it.startsWith("import kotlin.reflect.KClass") }
                .let { lines ->
                    val result: MutableList<String> = mutableListOf()
                    var index = 0
                    var line = lines[index++]
                    while (line.startsWith("import")) {
                        result.add(line)
                        line = lines[index++]
                    }
                    result.addAll(commonLines)
                    val subList = lines.subList(index, lines.lastIndex)
                    result.addAll(other)
                    result.addAll(subList)
                    result.add("Reflection.toString()")
                    return@let result
                }.joinToString(System.lineSeparator())

    }

    fun eval(): Any = scriptEngine.eval(filtered)

    private fun getResourceFile(filePath: String): File {
        val fileUrl = Script::class.java
                .classLoader
                .getResource(filePath)
        return File(fileUrl.toURI())
    }

    companion object {

        private const val TEST_FILES = "test-files"
        private const val GENERATOR = "generator"

        private val COMMON_KT = "$TEST_FILES${File.separator}$GENERATOR${File.separator}Common.kts"
        private val scriptEngine = ScriptEngineManager().getEngineByExtension("kts")

    }

}
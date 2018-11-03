package org.decembrist

import org.decembrist.browser.Browser
import org.decembrist.server.Server
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class PluginTest {

    private lateinit var server: Server

    private val port = 8000

    @AfterEach
    fun tearDown() {
        server.stop()
    }

    @Test
    fun mavenTest() {
        server = Server(mavenStaticPath, port).start()
        printStaticContent(mavenStaticPath, "Maven")
        val assertString = Browser(port).browse()
        assertEquals("OK! Assertions count: 31", assertString)
    }

    @Test
    fun gradleTest() {
        server = Server(gradleStaticPath, port).start()
        printStaticContent(gradleStaticPath, "Gradle")
        val assertString = Browser(port).browse()
        assertEquals("OK! Assertions count: 31", assertString)
    }

    @Test
    fun mavenPublishedTest() {
        server = Server(publishedMavenStaticPath, port).start()
        printStaticContent(mavenStaticPath, "Maven")
        val assertString = Browser(port).browse()
        assertEquals("OK! Assertions count: 31", assertString)
    }

    @Test
    fun gradlePublishedTest() {
        server = Server(publishedGradleStaticPath, port).start()
        printStaticContent(gradleStaticPath, "Gradle")
        val assertString = Browser(port).browse()
        assertEquals("OK! Assertions count: 31", assertString)
    }

    private fun printStaticContent(path: String, system: String) {
        val staticDirectory = File(path)
        val filesList = staticDirectory.listFiles().joinToString(transform = File::getName)
        println("$system test: static path: $path, files: $filesList")
    }

    companion object {
        val testProjectPath = System.getProperty("KOTLIN2JS_TEST_PROJECT")
        val publishedTestProjectPath = System.getProperty("KOTLIN2JS_PUBLISHED_TEST_PROJECT")
        val mavenStaticPath =
                "$testProjectPath${File.separatorChar}target${File.separatorChar}classes"
        val gradleStaticPath = "$testProjectPath${File.separatorChar}build"
        val publishedMavenStaticPath =
                "$testProjectPath${File.separatorChar}target${File.separatorChar}classes"
        val publishedGradleStaticPath = "$testProjectPath${File.separatorChar}build"
    }

}

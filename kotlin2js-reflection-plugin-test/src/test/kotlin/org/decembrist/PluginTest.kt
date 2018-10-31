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
        println("Maven test: static path: $mavenStaticPath")
        val assertString = Browser(port).browse()
        assertEquals("OK! Assertions count: 28", assertString)
    }

    @Test
    fun gradleTest() {
        server = Server(gradleStaticPath, port).start()
        println("Maven test: static path: $gradleStaticPath")
        val assertString = Browser(port).browse()
        assertEquals("OK! Assertions count: 28", assertString)
    }

    companion object {
        val testProjectPath = System.getProperty("KOTLIN2JS_TEST_PROJECT")
        val mavenStaticPath =
                "$testProjectPath${File.separatorChar}target${File.separatorChar}classes"
        val gradleStaticPath = "$testProjectPath${File.separatorChar}build"
    }

}

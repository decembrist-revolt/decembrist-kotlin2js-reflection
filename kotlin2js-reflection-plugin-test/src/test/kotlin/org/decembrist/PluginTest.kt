package org.decembrist

import org.decembrist.browser.Browser
import org.decembrist.server.Server
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class PluginTest {

    private lateinit var server: Server

    @AfterEach
    fun tearDown() {
        server.stop()
    }

    @Test
    fun mavenTest() {
        server = Server(mavenStaticPath).start()
        val assertString = Browser().browse()
        assertEquals("OK! Assertions count: 28", assertString)
    }

    @Test
    fun gradleTest() {
        server = Server(gradleStaticPath).start()
        val assertString = Browser().browse()
        assertEquals("OK! Assertions count: 28", assertString)
    }

    companion object {
        val testProjectPath = System.getProperty("KOTLIN2JS_TEST_PROJECT")
        val mavenStaticPath =
                "$testProjectPath${File.separatorChar}target${File.separatorChar}classes"
        val gradleStaticPath = "$testProjectPath${File.separatorChar}build"
    }

}

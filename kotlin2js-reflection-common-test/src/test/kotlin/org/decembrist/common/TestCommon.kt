package org.decembrist.common

import org.decembrist.parsers.SourceParserIntegrationTest
import org.decembrist.parsers.SourceParserTest
import org.decembrist.services.logging.Logger
import org.decembrist.services.logging.LoggerService
import org.junit.jupiter.api.BeforeAll
import java.io.File

abstract class TestCommon {

    abstract val testFilesFolder: String

    protected fun getResourceFile(filePath: String): File {
        val fileUrl = SourceParserIntegrationTest::class.java
                .classLoader
                .getResource(filePath)
        return File(fileUrl.toURI())
    }

    protected fun generatePath(fileName: String) = "$testFilesFolder$fileName"

    companion object {

        @BeforeAll
        @JvmStatic
        private fun beforeAll() {
            LoggerService.logger = object : Logger {
                override fun debug(message: String) {}
                override fun warn(message: String) {}
            }
        }

        open class OkFolder : File(SourceParserTest.TEST_FILE) {
            override fun exists() = true
            override fun isDirectory() = true
        }

    }

}
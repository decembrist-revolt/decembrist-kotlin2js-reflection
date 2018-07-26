package org.decembrist.parsers

import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.decembrist.domain.content.KtFileContent
import org.decembrist.fillers.AnnotationInfoFiller
import org.decembrist.fillers.FunctionFiller
import org.decembrist.services.logging.Logger
import org.decembrist.services.logging.LoggerService
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File
import java.util.Collections.singletonList

class SourceParserTest {

    @MockK
    lateinit var annotationInfoFiller: AnnotationInfoFiller

    @MockK
    lateinit var functionFiller: FunctionFiller

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    @DisplayName("class SourceParser should be initialized")
    fun checkSourcesSuccessTest() {
        val testFile1 = OkFolder()
        val testFile2 = OkFolder()
        SourceParser(listOf(testFile1, testFile2))
    }

    @Test
    @DisplayName("class SourceParser should throw exception")
    fun checkSourcesFailedUnExistssTest() {
        val testFile1 = UnexistedFolder()
        val testFile2 = UnexistedFolder()
        assertThrows<IllegalArgumentException> { SourceParser(listOf(testFile1, testFile2)) }
    }

    @Test
    @DisplayName("class SourceParser should throw exception")
    fun checkSourcesFailedNotFolderTest() {
        val testFile1 = ExistedFile()
        val testFile2 = ExistedFile()
        assertThrows<IllegalArgumentException> { SourceParser(listOf(testFile1, testFile2)) }
    }

    @Test
    @DisplayName("files should be parsed correctly")
    fun checkParseFileExtensions() {
        LoggerService.logger = object : Logger {
            override fun debug(message: String) {}
            override fun warn(message: String) {}
        }
        val krFileContent = KtFileContent("kotlin")
        val ktFile = ExistedFile("kotlin.kt")
        val nonKtFile = ExistedFile("kotlin.txt")
        val file = object : OkFolder() {
            override fun listFiles() = arrayOf(ktFile, nonKtFile)
        }
        mockkObject(KtFileParser.Companion)
        mockkObject(AnnotationInfoFiller.Companion)
        mockkObject(FunctionFiller.Companion)
        val ktFileParser = mockk<KtFileParser>()
        every {
            KtFileParser.of(ktFile)
        } returns ktFileParser
        every {
            ktFileParser.parse()
        } returns krFileContent
        val krFileContents = listOf(krFileContent)
        every {
            AnnotationInfoFiller.of(eq(krFileContents))
        } returns annotationInfoFiller
        every {
            FunctionFiller.of(eq(krFileContents))
        } returns functionFiller
        every {
            annotationInfoFiller.fill()
        } returns krFileContents
        every {
            functionFiller.fill()
        } returns krFileContents
        val sourceParser = SourceParser(singletonList(file))
        val result = sourceParser.parse()
        assertEquals(result, krFileContents)
        verify(exactly = 1) { ktFileParser.parse() }
    }

    open class UnexistedFolder : File(TEST_FILE) {
        override fun exists() = false
        override fun isDirectory() = true
    }

    open class OkFolder : File(TEST_FILE) {
        override fun exists() = true
        override fun isDirectory() = true
    }

    open class ExistedFile(fileName: String = TEST_FILE) : File(fileName) {
        override fun exists() = true
        override fun isDirectory() = false
    }

    companion object {

        const val TEST_FILE = "test_file"

    }

}

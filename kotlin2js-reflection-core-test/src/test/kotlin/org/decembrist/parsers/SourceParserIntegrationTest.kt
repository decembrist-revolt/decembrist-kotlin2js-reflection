package org.decembrist.parsers

import org.decembrist.common.TestCommon
import org.decembrist.common.TestCommon.Companion.OkFolder
import org.decembrist.domain.Attribute
import org.decembrist.services.logging.Logger
import org.decembrist.services.logging.LoggerService
import org.decembrist.services.typesuggestions.TypeSuggestion
import org.decembrist.services.typesuggestions.TypeSuggestion.Type
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import java.util.Collections.singletonList

class SourceParserIntegrationTest: TestCommon() {

    override val testFilesFolder = "$TEST_FILES${File.separator}$SOURCE_PARSER${File.separator}"

    val KT_FILE1 = "KtFile1.kt"
    val KT_FILE2 = "KtFile2.kt"
    val KT_FILE3 = "KtFile3.kt"
    val KT_FILE4 = "KtFile4.kt"
    val KT_FILE5 = "KtFile5.kt"

    val ktFile1 = getResourceFile(generatePath(KT_FILE1))
    val ktFile2 = getResourceFile(generatePath(KT_FILE2))
    val ktFile3 = getResourceFile(generatePath(KT_FILE3))
    val ktFile4 = getResourceFile(generatePath(KT_FILE4))
    val ktFile5 = getResourceFile(generatePath(KT_FILE5))

    @Test
    fun primitivesAnnotationParamsParseTest() {
        val folder = object : OkFolder() {
            override fun listFiles() = arrayOf(ktFile1)
        }
        val sourceParser = SourceParser(singletonList(folder))
        val fileContents = sourceParser.parse()
        assertEquals(1, fileContents.size)
        val fileContent = fileContents.first()
        val classes = fileContent.classes
        assertEquals(1, classes.size)
        val annotationClass = classes.first()
        assertEquals("TestAnnotation1", annotationClass.name)
        assertTrue(annotationClass.annotations.isEmpty())
        assertEquals(KT_FILE1, fileContent.name)
        assertEquals(1, fileContent.functions.size)
        val mainFunction = fileContent.functions.first()
        assertEquals("main", mainFunction.name)
        assertEquals(1, mainFunction.annotations.size)
        val annotationInstance = mainFunction.annotations.first()
        assertEquals(Type("TestAnnotation1", ""), annotationInstance.type)
        val attributes = annotationInstance.attributes
        assertEquals(9, attributes.size)
        val attribute1 = attributes[0]
        assertEquals(Attribute("string", "\"test\"", Type("String", "")), attribute1)
        val attribute2 = attributes[1]
        assertEquals(Attribute("byte", "1", Type("Byte", "")), attribute2)
        val attribute3 = attributes[2]
        assertEquals(Attribute("short", "1", Type("Short", "")), attribute3)
        val attribute4 = attributes[3]
        assertEquals(Attribute("int", "1", Type("Int", "")), attribute4)
        val attribute5 = attributes[4]
        assertEquals(Attribute("long", "1L", Type("Long", "")), attribute5)
        val attribute6 = attributes[5]
        assertEquals(Attribute("float", "1.0f", Type("Float", "")), attribute6)
        val attribute7 = attributes[6]
        assertEquals(Attribute("double", "1.0", Type("Double", "")), attribute7)
        val attribute8 = attributes[7]
        assertEquals(Attribute("char", "'t'", Type("Char", "")), attribute8)
        val attribute9 = attributes[8]
        assertEquals(Attribute("bool", "true", Type("Boolean", "")), attribute9)
    }

    @Test
    fun primitivesAnnotationArrayParamsParseTest() {
        val folder = object : OkFolder() {
            override fun listFiles() = arrayOf(ktFile2)
        }
        val sourceParser = SourceParser(singletonList(folder))
        val fileContents = sourceParser.parse()
        assertEquals(1, fileContents.size)
        val fileContent = fileContents.first()
        val classes = fileContent.classes
        assertEquals(1, classes.size)
        val annotationClass = classes.first()
        assertEquals("TestAnnotation2", annotationClass.name)
        assertTrue(annotationClass.annotations.isEmpty())
        assertEquals(KT_FILE2, fileContent.name)
        assertEquals(1, fileContent.functions.size)
        val mainFunction = fileContent.functions.first()
        assertEquals("main", mainFunction.name)
        assertEquals(1, mainFunction.annotations.size)
        val annotationInstance = mainFunction.annotations.first()
        assertEquals(Type("TestAnnotation2", ""), annotationInstance.type)
        val attributes = annotationInstance.attributes
        assertEquals(9, attributes.size)
        val attribute1 = attributes[0]
        val stringArrayType = TypeSuggestion.ProjectionContainer(
                Type("Array", ""),
                listOf(Type("String", ""))
        )
        assertEquals(Attribute("string", "arrayOf(\"test1\",\"test2\")", stringArrayType), attribute1)
        val attribute2 = attributes[1]
        assertEquals(Attribute("byte", "arrayOf(1,2)", Type("ByteArray", "")), attribute2)
        val attribute3 = attributes[2]
        assertEquals(Attribute("short", "arrayOf(1,2)", Type("ShortArray", "")), attribute3)
        val attribute4 = attributes[3]
        assertEquals(Attribute("int", "arrayOf(1,2)", Type("IntArray", "")), attribute4)
        val attribute5 = attributes[4]
        assertEquals(Attribute("long", "arrayOf(1L,2L)", Type("LongArray", "")), attribute5)
        val attribute6 = attributes[5]
        assertEquals(Attribute("float", "arrayOf(1.0f,2.0f)", Type("FloatArray", "")), attribute6)
        val attribute7 = attributes[6]
        assertEquals(Attribute("double", "arrayOf(1.0,2.0)", Type("DoubleArray", "")), attribute7)
        val attribute8 = attributes[7]
        assertEquals(Attribute("char", "arrayOf('1','2')", Type("CharArray", "")), attribute8)
        val attribute9 = attributes[8]
        assertEquals(Attribute("bool", "arrayOf(true,false)", Type("BooleanArray", "")), attribute9)
    }

    @Test
    fun primitivesAnnotationVarargParamsParseTest() {
        val folder = object : OkFolder() {
            override fun listFiles() = arrayOf(ktFile3)
        }
        val sourceParser = SourceParser(singletonList(folder))
        val fileContents = sourceParser.parse()
        assertEquals(1, fileContents.size)
        val fileContent = fileContents.first()
        val functions = fileContent.functions
        assertEquals(1, functions.size)
        val function = functions.first()
        val annotations = function.annotations

        val string = annotations.first { it.type.type == "StringVarargAnnotation" }
        assertEquals(1, string.attributes.size)
        val stringAttribute = string.attributes.first()
        assertEquals("string", stringAttribute.name)
        assertEquals("arrayOf(\"test1\", \"test2\")", stringAttribute.value)
        val stringArrayType = TypeSuggestion.ProjectionContainer(
                Type("Array", ""),
                listOf(Type("String", ""))
        )
        assertEquals(stringArrayType, stringAttribute.type)

        val byte = annotations.first { it.type.type == "ByteVarargAnnotation" }
        assertEquals(1, byte.attributes.size)
        val byteAttribute = byte.attributes.first()
        assertEquals("byte", byteAttribute.name)
        assertEquals("arrayOf(1, 2)", byteAttribute.value)
        val byteArrayType = Type("ByteArray", "kotlin")
        assertEquals(byteArrayType, byteAttribute.type)

        val short = annotations.first { it.type.type == "ShortVarargAnnotation" }
        assertEquals(1, short.attributes.size)
        val shortAttribute = short.attributes.first()
        assertEquals("short", shortAttribute.name)
        assertEquals("arrayOf(1, 2)", shortAttribute.value)
        val shortArrayType = Type("ShortArray", "kotlin")
        assertEquals(shortArrayType, shortAttribute.type)

        val int = annotations.first { it.type.type == "IntVarargAnnotation" }
        assertEquals(1, int.attributes.size)
        val intAttribute = int.attributes.first()
        assertEquals("int", intAttribute.name)
        assertEquals("arrayOf(1, 2)", intAttribute.value)
        val intArrayType = Type("IntArray", "kotlin")
        assertEquals(intArrayType, intAttribute.type)

        val long = annotations.first { it.type.type == "LongVarargAnnotation" }
        assertEquals(1, long.attributes.size)
        val longAttribute = long.attributes.first()
        assertEquals("long", longAttribute.name)
        assertEquals("arrayOf(1L, 2L)", longAttribute.value)
        val longArrayType = Type("LongArray", "kotlin")
        assertEquals(longArrayType, longAttribute.type)

        val float = annotations.first { it.type.type == "FloatVarargAnnotation" }
        assertEquals(1, float.attributes.size)
        val floatAttribute = float.attributes.first()
        assertEquals("float", floatAttribute.name)
        assertEquals("arrayOf(1.0f, 2.0f)", floatAttribute.value)
        val floatArrayType = Type("FloatArray", "kotlin")
        assertEquals(floatArrayType, floatAttribute.type)

        val double = annotations.first { it.type.type == "DoubleVarargAnnotation" }
        assertEquals(1, double.attributes.size)
        val doubleAttribute = double.attributes.first()
        assertEquals("double", doubleAttribute.name)
        assertEquals("arrayOf(1.0, 2.0)", doubleAttribute.value)
        val doubleArrayType = Type("DoubleArray", "kotlin")
        assertEquals(doubleArrayType, doubleAttribute.type)

        val char = annotations.first { it.type.type == "CharVarargAnnotation" }
        assertEquals(1, char.attributes.size)
        val charAttribute = char.attributes.first()
        assertEquals("char", charAttribute.name)
        assertEquals("arrayOf('1', '2')", charAttribute.value)
        val charArrayType = Type("CharArray", "kotlin")
        assertEquals(charArrayType, charAttribute.type)

        val boolean = annotations.first { it.type.type == "BooleanVarargAnnotation" }
        assertEquals(1, boolean.attributes.size)
        val booleanAttribute = boolean.attributes.first()
        assertEquals("bool", booleanAttribute.name)
        assertEquals("arrayOf(true, false)", booleanAttribute.value)
        val booleanArrayType = Type("BooleanArray", "kotlin")
        assertEquals(booleanArrayType, booleanAttribute.type)
    }

    @Test
    fun primitivesAnnotationTrickyVarargParamsParseTest() {
        val folder = object : OkFolder() {
            override fun listFiles() = arrayOf(ktFile4)
        }
        val sourceParser = SourceParser(singletonList(folder))
        val fileContents = sourceParser.parse()
        assertEquals(1, fileContents.size)
        val fileContent = fileContents.first()
        val functions = fileContent.functions
        assertEquals(1, functions.size)
        val function = functions.first()
        val annotations = function.annotations
        assertEquals(4, annotations.size)
        val attributes = annotations.flatMap { it.attributes }
        assertEquals(4, attributes.size)
        attributes.forEach { assertEquals("arrayOf(\"test1\",\"test2\")", it.value) }
        val stringArrayType = TypeSuggestion.ProjectionContainer(
                Type("Array", ""),
                listOf(Type("String", ""))
        )
        attributes.forEach { assertEquals(stringArrayType, it.type) }
        assertTrue(annotations.any { it.type.type == "SpreadVarargAnnotation" })
        assertTrue(annotations.any { it.type.type == "SquaredAnnotation" })
        assertTrue(annotations.any { it.type.type == "BracedAnnotation" })
        assertTrue(annotations.any { it.type.type == "SquaredVarargsAnnotation" })
    }

    @Test
    fun primitivesAnnotationStringTemplateParseTest() {
        val folder = object : OkFolder() {
            override fun listFiles() = arrayOf(ktFile5)
        }
        val sourceParser = SourceParser(singletonList(folder))
        val fileContents = sourceParser.parse()
        assertEquals(1, fileContents.size)
        val fileContent = fileContents.first()
        val functions = fileContent.functions
        assertEquals(1, functions.size)
        val function = functions.first()
        val annotations = function.annotations
        assertEquals(2, annotations.size)
        val attributes = annotations.flatMap { it.attributes }
        assertEquals(2, attributes.size)
        val string1 = annotations.first { it.type.type == "StringTemplate" }
        assertEquals(1, string1.attributes.size)
        val attribute1 = string1.attributes.first()
        assertEquals("\"\"\"template\"\"\"", attribute1.value)
        val string2 = annotations.first { it.type.type == "MultiStringTemplate" }
        assertEquals(1, string2.attributes.size)
        val attribute2 = string2.attributes.first()
        val sep = System.lineSeparator()
        assertEquals("\"\"\"$sep    multistring-template$sep\"\"\"", attribute2.value)
        val stringType = Type("String", "")
        attributes.forEach { assertEquals(stringType, it.type) }
    }

    companion object {

        private const val TEST_FILES = "test-files"
        private const val SOURCE_PARSER = "source-parser"

        @BeforeAll
        @JvmStatic
        private fun beforeAll() {
            LoggerService.logger = object : Logger {
                override fun debug(message: String) {}
                override fun warn(message: String) {}
            }
        }

    }

}
package org.decembrist.generators

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.decembrist.common.FunctionUtils
import org.decembrist.common.Script
import org.decembrist.common.TestCommon
import org.decembrist.common.TestCommon.Companion.OkFolder
import org.decembrist.parsers.SourceParser
import org.decembrist.services.logging.Logger
import org.decembrist.services.logging.LoggerService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*
import kotlin.reflect.KClass

class ReflectionUtilsGeneratorIntegrationTest : TestCommon() {

    override val testFilesFolder = "$TEST_FILES${File.separator}$GENERATOR${File.separator}"

    val KT_FILE1 = "KtFile1.kt"

    val ktFile1 = getResourceFile(generatePath(KT_FILE1))

    @Test
    fun hiderOrderedFunctionGenerationTest() {
        val folder = object : OkFolder() {
            override fun listFiles() = arrayOf(ktFile1)
        }
        val sourceParser = SourceParser(Collections.singletonList(folder))
        val fileContents = sourceParser.parse()
        val generated = ReflectionUtilsGenerator().generateCode(fileContents)
        assertEquals(1, generated.size)
        val fileText = generated.first().toString()
        val ktFileLines = ktFile1.readLines()
        val script = Script(fileText, ktFileLines)
        val json = script.eval().toString()
        val reflectionData = map(json, ReflectionData::class)
        val functions = reflectionData.functions
        assertEquals(1, functions.size)
        val function = functions.first().function
        val functionType = FunctionUtils.parseFunction(function.body)
        assertEquals("test", functionType.name)
        val paramTypes = functionType.paramTypes
        assertEquals(1, paramTypes.size)
        val paramType = paramTypes.component1()
        assertEquals(Triple::class.qualifiedName, paramType.clazz)
        val tripleSubTypes = paramType.subTypes
        assertEquals(3, tripleSubTypes.size)
        val tripleSubType1 = tripleSubTypes.component1()
        assertEquals(Map::class.qualifiedName, tripleSubType1.clazz)
        val mapSubTypes = tripleSubType1.subTypes
        assertEquals(2, mapSubTypes.size)
        val mapSubType1 = mapSubTypes.component1()
        assertEquals(List::class.qualifiedName, mapSubType1.clazz)
        val listSubTypes = mapSubType1.subTypes
        assertEquals(1, listSubTypes.size)
        val listSubType = listSubTypes.component1()
        assertEquals(String::class.qualifiedName, listSubType.clazz)
        val mapSubType2 = mapSubTypes.component2()
        assertEquals(Int::class.qualifiedName, mapSubType2.clazz)
        val tripleSubType2 = tripleSubTypes.component2()
        assertEquals(Set::class.qualifiedName, tripleSubType2.clazz)
        val setSubTypes = tripleSubType2.subTypes
        assertEquals(1, setSubTypes.size)
        val setSubType = setSubTypes.component1()
        assertEquals(Byte::class.qualifiedName, setSubType.clazz)
        val tripleSubType3 = tripleSubTypes.component3()
        assertEquals("A", fixScriptClassName(tripleSubType3.clazz))
        val returnType = functionType.returnType
        assertEquals(Unit::class.qualifiedName, returnType.clazz)
        assertEquals("test", function.name)
        val annotations = functions.first().annotations
        assertEquals(1, annotations.size)
        val annotation = annotations.first()
        assertEquals("Annotation1", fixScriptClassName(annotation.name))
        val params = annotation.params
        assertEquals(1, params.size)
        val param = params.first()
        assertEquals("name", param.name)
        assertEquals(String::class.qualifiedName, param.type)
        assertEquals("test1", param.value)
    }

    private fun <T : Any> map(json: String, clazz: KClass<T>): T {
        val mapper = jacksonObjectMapper()
        return mapper.readValue(json, clazz.java)
    }

    companion object {

        private const val TEST_FILES = "test-files"
        private const val GENERATOR = "generator"

        @BeforeAll
        @JvmStatic
        private fun beforeAll() {
            LoggerService.logger = object : Logger {
                override fun debug(message: String) {}
                override fun warn(message: String) {}
            }
        }

        fun fixScriptClassName(name: String) = name.split(".").component2()

        open class ReflectionData(val functions: Array<FunctionBlock>)

        open class FunctionBlock(val function: FunctionIdentifier, val annotations: Array<Annotation>)

        open class FunctionIdentifier(val name: String, val body: String)

        open class Annotation(val name: String, val params: Array<AnnotationParam>)

        open class AnnotationParam(val name: String, val type: String, val value: String)

    }

}
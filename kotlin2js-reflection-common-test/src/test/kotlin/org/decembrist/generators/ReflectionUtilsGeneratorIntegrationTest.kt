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
    val KT_FILE2 = "KtFile2.kt"
    val KT_FILE3 = "KtFile3.kt"
    val KT_FILE4 = "KtFile4.kt"

    val ktFile1 = getResourceFile(generatePath(KT_FILE1))
    val ktFile2 = getResourceFile(generatePath(KT_FILE2))
    val ktFile3 = getResourceFile(generatePath(KT_FILE3))
    val ktFile4 = getResourceFile(generatePath(KT_FILE4))

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
        assertEquals("test1", param.value.first())
    }

    @Test
    fun hiderOrderedFunctionAnnotationPlainTypesGenerationTest() {
        val folder = object : OkFolder() {
            override fun listFiles() = arrayOf(ktFile2)
        }
        val sourceParser = SourceParser(Collections.singletonList(folder))
        val fileContents = sourceParser.parse()
        val generated = ReflectionUtilsGenerator().generateCode(fileContents)
        assertEquals(1, generated.size)
        val fileText = generated.first().toString()
        val ktFileLines = ktFile2.readLines()
        val script = Script(fileText, ktFileLines)
        val json = script.eval().toString()
        val reflectionData = map(json, ReflectionData::class)
        val functions = reflectionData.functions
        assertEquals(1, functions.size)
        val function = functions.first().function
        val functionType = FunctionUtils.parseFunction(function.body)
        assertEquals("main", functionType.name)
        val paramTypes = functionType.paramTypes
        assertEquals(1, paramTypes.size)
        val paramType = paramTypes.first()
        assertEquals(FunctionUtils.Type(
                Array<String>::class.qualifiedName!!,
                listOf(
                        FunctionUtils.Type(
                                String::class.qualifiedName!!
                        )
                )
        ), paramType)
        val returnType = functionType.returnType
        assertEquals(Unit::class.qualifiedName, returnType.clazz)
        val annotations = functions.first().annotations
        assertEquals(1, annotations.size)
        val annotation = annotations.first()
        assertEquals("Annotation2", fixScriptClassName(annotation.name))
        val params = annotation.params
        val stringParam = params[0]
        assertEquals("string", stringParam.name)
        assertEquals(String::class.qualifiedName, stringParam.type)
        assertEquals("test1", stringParam.value.first())
        val byteParam = params[1]
        assertEquals("byte", byteParam.name)
        assertEquals(Byte::class.qualifiedName, byteParam.type)
        assertEquals("1", byteParam.value.first())
        val shortParam = params[2]
        assertEquals("short", shortParam.name)
        assertEquals(Short::class.qualifiedName, shortParam.type)
        assertEquals("1", shortParam.value.first())
        val intParam = params[3]
        assertEquals("int", intParam.name)
        assertEquals(Int::class.qualifiedName, intParam.type)
        assertEquals("1", intParam.value.first())
        val longParam = params[4]
        assertEquals("long", longParam.name)
        assertEquals(Long::class.qualifiedName, longParam.type)
        assertEquals("1", longParam.value.first())
        val floatParam = params[5]
        assertEquals("float", floatParam.name)
        assertEquals(Float::class.qualifiedName, floatParam.type)
        assertEquals("1.0", floatParam.value.first())
        val doubleParam = params[6]
        assertEquals("double", doubleParam.name)
        assertEquals(Double::class.qualifiedName, doubleParam.type)
        assertEquals("1.0", doubleParam.value.first())
        val charParam = params[7]
        assertEquals("char", charParam.name)
        assertEquals(Char::class.qualifiedName, charParam.type)
        assertEquals("t", charParam.value.first())
        val boolParam = params[8]
        assertEquals("bool", boolParam.name)
        assertEquals(Boolean::class.qualifiedName, boolParam.type)
        assertEquals("true", boolParam.value.first())
    }

    @Test
    fun hiderOrderedFunctionAnnotationArrayTypesGenerationTest() {
        val folder = object : OkFolder() {
            override fun listFiles() = arrayOf(ktFile3)
        }
        val sourceParser = SourceParser(Collections.singletonList(folder))
        val fileContents = sourceParser.parse()
        val generated = ReflectionUtilsGenerator().generateCode(fileContents)
        assertEquals(1, generated.size)
        val fileText = generated.first().toString()
        val ktFileLines = ktFile3.readLines()
        val script = Script(fileText, ktFileLines)
        val json = script.eval().toString()
        val reflectionData = map(json, ReflectionData::class)
        val functions = reflectionData.functions
        assertEquals(1, functions.size)
        val function = functions.first().function
        val functionType = FunctionUtils.parseFunction(function.body)
        assertEquals("main", functionType.name)
        val paramTypes = functionType.paramTypes
        assertEquals(1, paramTypes.size)
        val paramType = paramTypes.first()
        assertEquals(FunctionUtils.Type(
                Array<String>::class.qualifiedName!!,
                listOf(
                        FunctionUtils.Type(
                                String::class.qualifiedName!!
                        )
                )
        ), paramType)
        val returnType = functionType.returnType
        assertEquals(Unit::class.qualifiedName, returnType.clazz)
        val annotations = functions.first().annotations
        assertEquals(1, annotations.size)
        val annotation = annotations.first()
        assertEquals("Annotation3", fixScriptClassName(annotation.name))
        val params = annotation.params
        val stringParam = params[0]
        assertEquals("string", stringParam.name)
        assertEquals(arrayQualifiedName(String::class), stringParam.type)
        assertEquals("test1", stringParam.value.component1())
        assertEquals("test2", stringParam.value.component2())
        val byteParam = params[1]
        assertEquals("byte", byteParam.name)
        assertEquals(ByteArray::class.qualifiedName, byteParam.type)
        assertEquals("1", byteParam.value.component1())
        assertEquals("2", byteParam.value.component2())
        val shortParam = params[2]
        assertEquals("short", shortParam.name)
        assertEquals(ShortArray::class.qualifiedName, shortParam.type)
        assertEquals("1", shortParam.value.component1())
        assertEquals("2", shortParam.value.component2())
        val intParam = params[3]
        assertEquals("int", intParam.name)
        assertEquals(IntArray::class.qualifiedName, intParam.type)
        assertEquals("1", intParam.value.component1())
        assertEquals("2", intParam.value.component2())
        val longParam = params[4]
        assertEquals("long", longParam.name)
        assertEquals(LongArray::class.qualifiedName, longParam.type)
        assertEquals("1", longParam.value.component1())
        assertEquals("2", longParam.value.component2())
        val floatParam = params[5]
        assertEquals("float", floatParam.name)
        assertEquals(FloatArray::class.qualifiedName, floatParam.type)
        assertEquals("1.0", floatParam.value.component1())
        assertEquals("2.0", floatParam.value.component2())
        val doubleParam = params[6]
        assertEquals("double", doubleParam.name)
        assertEquals(DoubleArray::class.qualifiedName, doubleParam.type)
        assertEquals("1.0", doubleParam.value.component1())
        assertEquals("2.0", doubleParam.value.component2())
        val charParam = params[7]
        assertEquals("char", charParam.name)
        assertEquals(CharArray::class.qualifiedName, charParam.type)
        assertEquals("1", charParam.value.component1())
        assertEquals("2", charParam.value.component2())
        val boolParam = params[8]
        assertEquals("bool", boolParam.name)
        assertEquals(BooleanArray::class.qualifiedName, boolParam.type)
        assertEquals("true", boolParam.value.component1())
        assertEquals("false", boolParam.value.component2())
    }

    @Test
    fun hiderOrderedFunctionVarargTypesGenerationTest() {
        val folder = object : OkFolder() {
            override fun listFiles() = arrayOf(ktFile4)
        }
        val sourceParser = SourceParser(Collections.singletonList(folder))
        val fileContents = sourceParser.parse()
        val generated = ReflectionUtilsGenerator().generateCode(fileContents)
        assertEquals(1, generated.size)
        val fileText = generated.first().toString()
        val ktFileLines = ktFile4.readLines()
        val script = Script(fileText, ktFileLines)
        val json = script.eval().toString()
        val reflectionData = map(json, ReflectionData::class)
        val functions = reflectionData.functions
        assertEquals(1, functions.size)
        val function = functions.first().function
        val functionType = FunctionUtils.parseFunction(function.body)
        assertEquals("main", functionType.name)
        val paramTypes = functionType.paramTypes
        assertEquals(1, paramTypes.size)
        val paramType = paramTypes.first()
        assertEquals(FunctionUtils.Type(
                Array<String>::class.qualifiedName!!,
                listOf(
                        FunctionUtils.Type(
                                String::class.qualifiedName!!
                        )
                )
        ), paramType)
        val returnType = functionType.returnType
        assertEquals(Unit::class.qualifiedName, returnType.clazz)
        val annotations = functions.first().annotations
        assertEquals(9, annotations.size)
        val annotation1 = annotations[0]
        assertEquals("Annotation4", fixScriptClassName(annotation1.name))
        val stringParam = annotation1.params[0]
        assertEquals("string", stringParam.name)
        assertEquals("kotlin.Array<out kotlin.String>", stringParam.type)
        assertEquals("test1", stringParam.value.component1())
        assertEquals("test2", stringParam.value.component2())
        val annotation2 = annotations[1]
        assertEquals("Annotation5", fixScriptClassName(annotation2.name))
        val byteParam = annotation2.params[0]
        assertEquals("byte", byteParam.name)
        assertEquals(ByteArray::class.qualifiedName, byteParam.type)
        assertEquals("1", byteParam.value.component1())
        assertEquals("2", byteParam.value.component2())
        val annotation3 = annotations[2]
        assertEquals("Annotation6", fixScriptClassName(annotation3.name))
        val shortParam = annotation3.params[0]
        assertEquals("short", shortParam.name)
        assertEquals(ShortArray::class.qualifiedName, shortParam.type)
        assertEquals("1", shortParam.value.component1())
        assertEquals("2", shortParam.value.component2())
        val annotation4 = annotations[3]
        assertEquals("Annotation7", fixScriptClassName(annotation4.name))
        val intParam = annotation4.params[0]
        assertEquals("int", intParam.name)
        assertEquals(IntArray::class.qualifiedName, intParam.type)
        assertEquals("1", intParam.value.component1())
        assertEquals("2", intParam.value.component2())
        val annotation5 = annotations[4]
        assertEquals("Annotation8", fixScriptClassName(annotation5.name))
        val longParam = annotation5.params[0]
        assertEquals("long", longParam.name)
        assertEquals(LongArray::class.qualifiedName, longParam.type)
        assertEquals("1", longParam.value.component1())
        assertEquals("2", longParam.value.component2())
        val annotation6 = annotations[5]
        assertEquals("Annotation9", fixScriptClassName(annotation6.name))
        val floatParam = annotation6.params[0]
        assertEquals("float", floatParam.name)
        assertEquals(FloatArray::class.qualifiedName, floatParam.type)
        assertEquals("1.0", floatParam.value.component1())
        assertEquals("2.0", floatParam.value.component2())
        val annotation7 = annotations[6]
        assertEquals("Annotation10", fixScriptClassName(annotation7.name))
        val doubleParam = annotation7.params[0]
        assertEquals("double", doubleParam.name)
        assertEquals(DoubleArray::class.qualifiedName, doubleParam.type)
        assertEquals("1.0", doubleParam.value.component1())
        assertEquals("2.0", doubleParam.value.component2())
        val annotation8 = annotations[7]
        assertEquals("Annotation11", fixScriptClassName(annotation8.name))
        val charParam = annotation8.params[0]
        assertEquals("char", charParam.name)
        assertEquals(CharArray::class.qualifiedName, charParam.type)
        assertEquals("1", charParam.value.component1())
        assertEquals("2", charParam.value.component2())
        val annotation9 = annotations[8]
        assertEquals("Annotation12", fixScriptClassName(annotation9.name))
        val boolParam = annotation9.params[0]
        assertEquals("bool", boolParam.name)
        assertEquals(BooleanArray::class.qualifiedName, boolParam.type)
        assertEquals("true", boolParam.value.component1())
        assertEquals("false", boolParam.value.component2())
    }

    private fun <T : Any> map(json: String, clazz: KClass<T>): T {
        val mapper = jacksonObjectMapper()
        return mapper.readValue(json, clazz.java)
    }

    private fun arrayQualifiedName(clazz: KClass<*>): String {
        val array = Array<Any>::class.qualifiedName
        return "$array<${clazz.qualifiedName}>"
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

        open class AnnotationParam(val name: String, val type: String, val value: Array<String>)

    }

}
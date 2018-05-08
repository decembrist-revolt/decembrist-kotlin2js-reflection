package org.decembrist.generators

import com.squareup.kotlinpoet.*
import org.decembrist.Message.nainClassNotFoundMessage
import org.decembrist.domain.Attribute
import org.decembrist.domain.content.KtFileContent
import org.decembrist.domain.content.functions.HiderOrderFunc
import java.util.*

class ReflectionUtilsGenerator(private val mainClass: String) {

    private val reflectionUUID = "r" + UUID.randomUUID().toString().replace("-", "")

    private val fileName = "reflection$reflectionUUID"

    fun generateCode(ktFileContents: List<KtFileContent>): List<FileSpec> {
        val mainClassFile = findMainClassFile(ktFileContents)
        val packageName = mainClassFile.`package`?.name ?: ""
        val reflectionFunction = generateReflectionExtensionFunc(mainClassFile)
        val fileSpec = FileSpec.builder(packageName, fileName)
                .addProperty(generateReflectionObjProp())
                .addFunction(reflectionFunction)
                .addStaticImport(JS_REFLECT_CLASSPATH, "jsReflect")
                .build()
        return listOf(fileSpec)
    }

    private fun findMainClassFile(ktFileContents: List<KtFileContent>) = ktFileContents
            .firstOrNull { ktFileContent ->
                val packageName = if (ktFileContent.`package`?.hasPackage()!!) {
                    ktFileContent.`package`!!.name + "."
                } else ""
                val className = if (".kt" in ktFileContent.name) {
                    ktFileContent.name.replace(".kt", "")
                } else ktFileContent.name
                "$packageName$className" == mainClass
            } ?: throw IllegalArgumentException(nainClassNotFoundMessage(mainClass))

    private fun generateReflectionObjProp() = PropertySpec
            .builder("reflection", DYNAMIC, KModifier.PRIVATE)
            .mutable(false)
            .initializer(CodeBlock.of("""js("{}")"""))
            .build()

    private fun generateReflectionExtensionFunc(ktFileContents: KtFileContent): FunSpec {
        val startFunction = CodeBlock.builder()
                .addStatement("""val annotations: dynamic = js("{}")""")
                .build()
        val funcCodeBlock = FunSpec
                .builder("reflection")
                .returns(REFLECTION_TYPE)
                .receiver(MAIN_FUNCTION_TYPE)
                .addCode(startFunction)
        val funcAnnotationsBlocks = ktFileContents.functions
                .filter { it is HiderOrderFunc }
                .map { generateHOFuncReflectionCodeBlock(it as HiderOrderFunc) }
        for (funcAnnotationsBlock in funcAnnotationsBlocks) {
            funcCodeBlock.addCode(funcAnnotationsBlock)
        }
        funcCodeBlock.addStatement("return %T.of(annotations)", REFLECTION_TYPE)
        return funcCodeBlock.build()
    }

    fun generateHOFuncReflectionCodeBlock(hiderOrderFunc: HiderOrderFunc): CodeBlock {
        val funcName = hiderOrderFunc.name
        val annotationsBlock = CodeBlock.builder()
                .add("annotations[::$funcName.toString()] = ")
        if (hiderOrderFunc.annotations.isEmpty()) {
            annotationsBlock.addStatement("emptyList<Annotation>()")
        } else {
            annotationsBlock.addStatement("listOf<Annotation>(")
            var needComma = false
            for (annotation in hiderOrderFunc.annotations) {
                if (needComma) annotationsBlock.addStatement(",")
                val annotationType = annotation.type.toTypeName()
                val attributesBlock = makeAttributes(annotation.attributes)
                annotationsBlock.add("  %T::class.jsReflect.createInstance(", annotationType)
                annotationsBlock.add(attributesBlock)
                annotationsBlock.add(")")
                needComma = true
            }
            annotationsBlock.addStatement("")
            annotationsBlock.addStatement(")")
        }
        return annotationsBlock.build()
    }

    private fun makeAttributes(attributes: MutableList<Attribute>): CodeBlock {
        val result = CodeBlock.builder().add("arrayOf(")
        val attributesString = attributes.joinToString { attribute ->
            if (attribute.type.type == "String") "\"${attribute.value}\"" else attribute.value
        }
        result.add(attributesString)
        result.add(")")
        return result.build()
    }

    companion object {
        val JS_REFLECT_CLASSPATH = ClassName("org.decembrist", "utils")
        val STRING_TYPE = ClassName("", "String")
        val REFLECTION_TYPE = ClassName("org.decembrist.reflection", "Reflection")
        val KFUNCTION1_TYPE = ClassName("kotlin.reflect", "KFunction1")
        val ARRAY_TYPE = ClassName("", "Array")
        val UNIT_TYPE = ClassName("", "Unit")
        val MAIN_FUNCTION_TYPE = ParameterizedTypeName
                .get(
                        KFUNCTION1_TYPE,
                        ParameterizedTypeName.get(ARRAY_TYPE, STRING_TYPE),
                        UNIT_TYPE
                )
    }

}
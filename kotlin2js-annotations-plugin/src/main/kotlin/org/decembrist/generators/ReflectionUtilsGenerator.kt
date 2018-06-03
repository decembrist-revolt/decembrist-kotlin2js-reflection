package org.decembrist.generators

import com.squareup.kotlinpoet.*
import org.decembrist.Message.nainClassNotFoundMessage
import org.decembrist.domain.content.KtFileContent
import org.decembrist.domain.content.functions.HiderOrderFunc
import org.decembrist.generators.annotations.ClassGenerator
import org.decembrist.generators.annotations.HiderOrderFunctionAnnotationsGenerator
import java.util.*

class ReflectionUtilsGenerator(private val mainClass: String) {

    private val reflectionUUID = UUID.randomUUID()
            .toString()
            .replace("-", "")

    private val fileName = "Reflection$reflectionUUID"

    fun generateCode(ktFileContents: Collection<KtFileContent>): List<FileSpec> {
        val mainClassFile = findMainClassFile(ktFileContents)
        val packageName = mainClassFile.`package`?.name ?: ""
        val fileSpec = FileSpec.builder(packageName, fileName)
                .addStaticImport(JS_REFLECT_CLASSPATH, "jsReflect")
                .addStaticImport(JS_REFLECT_CLASSPATH, "putAndCheck")
                .addStaticImport(JS_REFLECT_CLASSPATH, "getIdentifier")
                .addProperty(generateReflectionVal(mainClassFile))
                .build()
        return listOf(fileSpec)
    }

    private fun findMainClassFile(ktFileContents: Collection<KtFileContent>) = ktFileContents
            .firstOrNull { ktFileContent ->
                val packageName = if (ktFileContent.`package`?.hasPackage()!!) {
                    ktFileContent.`package`!!.name + "."
                } else ""
                val className = if (".kt" in ktFileContent.name) {
                    ktFileContent.name.replace(".kt", "")
                } else ktFileContent.name
                "$packageName$className" == mainClass
            } ?: throw IllegalArgumentException(nainClassNotFoundMessage(mainClass))

    private fun generateReflectionVal(ktFileContents: KtFileContent) = PropertySpec
            .builder("reflection$reflectionUUID", UNIT, KModifier.PRIVATE)
            .mutable(false)
            .initializer(generateReflectionValInitializer(ktFileContents))
            .build()

    private fun generateReflectionValInitializer(ktFileContent: KtFileContent): CodeBlock {
        val initializer = CodeBlock.builder()
                .add("{")
                .nextLine()
        processHiderOrderedFunctionsInfo(ktFileContent, initializer)
        initializer.nextLine()
        initializer
                .add("%T.setData(functionAnnotations)", REFLECTION_TYPE)
                .nextLine()
                .add("}()")
        return initializer.build()
    }

    private fun processHiderOrderedFunctionsInfo(ktFileContent: KtFileContent,
                                                 codeBuilder: CodeBlock.Builder) {
        codeBuilder.add("val functionAnnotations = HashMap<String, List<Annotation>>()")
                .nextLine()
        val funcAnnotationsBlocks = ktFileContent.functions
                .filter { it is HiderOrderFunc }
                .map { HiderOrderFunctionAnnotationsGenerator.generate(it as HiderOrderFunc) }
        for (funcAnnotationsBlock in funcAnnotationsBlocks) {
            codeBuilder.add(funcAnnotationsBlock)
        }
    }

    private fun processClassesInfo(ktFileContent: KtFileContent,
                                   codeBuilder: CodeBlock.Builder) {
        codeBuilder.add("val classes: List<ClassInfo<*>> = listOf(")
                .nextLine()
        val packageName = ktFileContent.`package`?.name ?: ""
        val classAnnotationsBlock = ktFileContent.classes
                .map { ClassGenerator(packageName).generate(it) }
    }

    companion object {
        val JS_REFLECT_CLASSPATH = ClassName("org.decembrist", "utils")
        val STRING_TYPE = ClassName("", "String")
        val REFLECTION_TYPE = ClassName("org.decembrist.reflection", "Reflection")
        val KFUNCTION1_TYPE = ClassName("kotlin.reflect", "KFunction1")
        val ARRAY_TYPE = ClassName("", "Array")
        val MAIN_FUNCTION_TYPE = ParameterizedTypeName
                .get(
                        KFUNCTION1_TYPE,
                        ParameterizedTypeName.get(ARRAY_TYPE, STRING_TYPE),
                        UNIT
                )
    }

}
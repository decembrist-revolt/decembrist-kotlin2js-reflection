package org.decembrist.generators

import com.squareup.kotlinpoet.*
import org.decembrist.Message.nainClassNotFoundMessage
import org.decembrist.domain.content.KtFileContent
import org.decembrist.domain.content.functions.HiderOrderFunc
import org.decembrist.generators.annotations.ClassGenerator
import org.decembrist.generators.annotations.HiderOrderFunctionAnnotationsGenerator
import java.util.*

class ReflectionUtilsGenerator(private val mainClass: String) {

    fun generateCode(ktFileContents: Collection<KtFileContent>): List<FileSpec> {
        val mainClassFile = findMainClassFile(ktFileContents)
        return ktFileContents.map(this::generateCode)
    }

    private fun generateCode(fileContent: KtFileContent): FileSpec {
        val packageName = fileContent.`package`?.name ?: ""
        val reflectionUUID = UUID.randomUUID()
                .toString()
                .replace("-", "")
        val fileName = "Reflection$reflectionUUID"
        val mainReflectionVal = generateReflectionVal(fileContent, reflectionUUID)
        val fileSpecBuilder = FileSpec.builder(packageName, fileName)
                .addStaticImport(JS_REFLECT_CLASSPATH, "jsReflect")
                .addStaticImport(JS_REFLECT_CLASSPATH, "putAndCheck")
                .addStaticImport(JS_REFLECT_CLASSPATH, "getIdentifier")
                .addProperty(mainReflectionVal)
        addAliasedImports(fileContent, fileSpecBuilder)
        return fileSpecBuilder.build()
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

    private fun generateReflectionVal(ktFileContents: KtFileContent,
                                      reflectionUUID: String) = PropertySpec
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
        processClassesInfo(ktFileContent, initializer)
        initializer
                .nextLine()
                .add("%T.setData(functionAnnotations, classes)", REFLECTION_TYPE)
                .nextLine()
                .add("}()")
        return initializer.build()
    }

    /**
     * Process hider-ordered function reflection information
     */
    private fun processHiderOrderedFunctionsInfo(ktFileContent: KtFileContent,
                                                 codeBuilder: CodeBlock.Builder) {
        codeBuilder
                .add("val functionAnnotations")
                .add(
                        " = %T<%T, %T<Annotation>>()",
                        MUTABLE_MAP_OF_FUNCTION,
                        FUNCTION_IDENTIFIER,
                        LIST_TYPE
                ).nextLine()
        val funcAnnotationsBlocks = ktFileContent.functions
                .filter { it is HiderOrderFunc }
                .map { HiderOrderFunctionAnnotationsGenerator.generate(it as HiderOrderFunc) }
        for (funcAnnotationsBlock in funcAnnotationsBlocks) {
            codeBuilder.add(funcAnnotationsBlock)
        }
    }

    /**
     * Process class reflection information
     */
    private fun processClassesInfo(ktFileContent: KtFileContent,
                                   codeBuilder: CodeBlock.Builder) {
        codeBuilder.add(
                "val classes: %T<%T<*>> = %T(",
                LIST_TYPE,
                ClassGenerator.CLASS_INFO_TYPE,
                LIST_OF_FUNCTION
        ).nextLine()
        val packageName = ktFileContent.`package`?.name ?: ""
        val classBlocks = ktFileContent.classes
                .map { ClassGenerator(packageName).generate(it) }
        val classInfoBlock = CodeBlock.builder()
        for (classBlock in classBlocks) {
            if (classInfoBlock.isNotEmpty()) {
                classInfoBlock
                        .add(",")
                        .nextLine()
            }
            classInfoBlock.add(classBlock)
        }
        codeBuilder
                .indent()
                .add(classInfoBlock.build())
                .unindent()
                .nextLine()
                .add(")")
    }

    /**
     * Add imports with aliases to result reflection file
     */
    private fun addAliasedImports(fileContent: KtFileContent, fileBuilder: FileSpec.Builder) {
        val aliasedImports = fileContent.imports
                ?.imports
                ?.filter { it.alias != null }
                .orEmpty()
        for (aliasedImport in aliasedImports) {
            val fullClassName = aliasedImport.className
            val className = ClassName("", fullClassName)
            val alias = aliasedImport.alias!!
            fileBuilder.addAliasedImport(className, alias)
        }
    }

    companion object {

        val JS_REFLECT_CLASSPATH = ClassName("org.decembrist", "utils")
        val REFLECTION_TYPE = ClassName("org.decembrist.reflection", "Reflection")
        val FUNCTION_IDENTIFIER = ClassName("org.decembrist.model", "FunctionIdentifier")
        val LIST_TYPE = ClassName("kotlin.collections", "List")
        val LIST_OF_FUNCTION = ClassName("kotlin.collections", "listOf")
        val MUTABLE_MAP_OF_FUNCTION = ClassName("kotlin.collections",
                "mutableMapOf")

    }

}
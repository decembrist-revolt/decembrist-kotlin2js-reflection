package org.decembrist.generators.annotations

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import org.decembrist.domain.content.annotations.AnnotationClass
import org.decembrist.domain.content.classes.AbstractClass
import org.decembrist.domain.content.classes.Class
import org.decembrist.domain.content.members.Method
import org.decembrist.generators.IGenerator
import org.decembrist.generators.nextLine

class ClassGenerator(val packageName: String) : IGenerator<AbstractClass> {

    override fun generate(content: AbstractClass): CodeBlock {
        val className = content.name
        val classType = ClassName(packageName, className)
        val methodInfoGenerator = MethodInfoGenerator(classType)
        val codeBlock = CodeBlock.builder()
                .add("%T(", CLASS_INFO_TYPE)
                .nextLine()
                .indent()
                .add("%T::class,", classType)
                .nextLine()
        val methodsBlock: CodeBlock = when (content) {
            is Class -> generateMethodsBlock(methodInfoGenerator, content.methods)
            is AnnotationClass -> CodeBlock.of("emptyList()")
            else -> throw UnsupportedOperationException(
                    "Class generator for ${content::class} unsupported yet")
        }
        val annotationsBlock: CodeBlock = AnnotationBlockGenerator.generate(content)
        codeBlock
                .add(methodsBlock)
                .add(",")
                .nextLine()
                .add(annotationsBlock)
                .nextLine()
                .unindent()
                .add(")")
        val classEmpty = isClassEmpty(methodsBlock, annotationsBlock)
        return if (classEmpty) CodeBlock.of("") else codeBlock.build()
    }

    private fun generateMethodsBlock(methodInfoGenerator: MethodInfoGenerator,
                                     methods: MutableSet<Method>): CodeBlock {
        return if (methods.isNotEmpty()) {
            val generatedBlock = CodeBlock.builder()
                    .add("listOf(")
                    .nextLine()
                    .indent()
            val methodInfoBlocks = methods
                    .map { methodInfoGenerator.generate(it) }
            for (index in 0 until methodInfoBlocks.size) {
                if (index > 0) {
                    generatedBlock
                            .add(",")
                            .nextLine()
                }
                val methodInfoBlock = methodInfoBlocks[index]
                generatedBlock.add(methodInfoBlock)
            }
            generatedBlock
                    .nextLine()
                    .unindent()
                    .add(")")
                    .build()
        } else CodeBlock.builder()
                .add("emptyList()")
                .build()
    }

    fun isClassEmpty(methodsBlock: CodeBlock, annotationsBlock: CodeBlock): Boolean {
        return methodsBlock.toString() == "emptyList()"
                && annotationsBlock.toString() == "emptyList<Annotation>()"
    }

    companion object {

        val CLASS_INFO_TYPE = ClassName("org.decembrist.model", "ClassInfo")

    }

}
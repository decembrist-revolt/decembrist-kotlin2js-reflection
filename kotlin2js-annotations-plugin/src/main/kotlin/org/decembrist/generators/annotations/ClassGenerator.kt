package org.decembrist.generators.annotations

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import org.decembrist.domain.Attribute
import org.decembrist.domain.content.annotations.AnnotationClass
import org.decembrist.domain.content.classes.AbstractClass
import org.decembrist.domain.content.classes.Class
import org.decembrist.domain.content.functions.HiderOrderFunc
import org.decembrist.domain.content.members.Method
import org.decembrist.generators.IGenerator
import org.decembrist.generators.nextLine

class ClassGenerator(val packageName: String) : IGenerator<AbstractClass> {

    override fun generate(content: AbstractClass): CodeBlock {
        val className = content.name
        val classType = ClassName(packageName, className)
        CodeBlock.builder()
                .add("ClassInfo(")
                .indent()
                .add("%T::class,", classType)
        val methodsBlock: CodeBlock = when (content) {
            is Class -> generateMethodsBlock(content.methods)
            is AnnotationClass -> CodeBlock.of("emptyList()")
            else -> throw UnsupportedOperationException(
                    "Class generator for ${content::class} unsupported yet")
        }

        val annotationsBlock = CodeBlock.builder()
                .add("functionAnnotations.putAndCheck(")
                .nextLine()
                .indent()
                .add("getIdentifier(::$className),")
                .nextLine()
        if (content.annotations.isEmpty()) {
            annotationsBlock
                    .add("emptyList<Annotation>()")
                    .nextLine()
        } else {
            annotationsBlock
                    .add("listOf<Annotation>(")
                    .nextLine()
                    .indent()
            var needComma = false
            for (annotation in content.annotations) {
                if (needComma) annotationsBlock
                        .add(",")
                        .nextLine()
                val annotationType = annotation.type.toTypeName()
                val attributesBlock = makeAttributes(annotation.attributes)
                annotationsBlock
                        .add("%T::class.jsReflect.createInstance(", annotationType)
                        .add(attributesBlock)
                        .add(")")
                needComma = true
            }
            annotationsBlock
                    .nextLine()
                    .unindent()
                    .add(")")
                    .nextLine()
                    .unindent()
                    .add(")")
                    .nextLine()
        }
        return annotationsBlock.build()
    }

    private fun generateMethodsBlock(methods: Set<Method>): CodeBlock {
        return if (methods.isNotEmpty()) {
            val generatedBlock = CodeBlock.builder()
            val blocks = methods
                    .map { MethodInfoGenerator.generate(it) }
            for (block in blocks) {
                if (generatedBlock.isEmpty()) {
                    generatedBlock
                            .add(",")
                            .indent()
                }
                generatedBlock.add(block)
            }
            generatedBlock.build()
        } else CodeBlock.of("emptyList(),")
    }

    private fun generateAnnotations(): CodeBlock {
        TODO()
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

}
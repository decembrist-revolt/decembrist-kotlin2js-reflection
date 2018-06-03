package org.decembrist.generators.annotations

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import org.decembrist.domain.Attribute
import org.decembrist.domain.content.classes.AbstractClass
import org.decembrist.domain.content.classes.Class
import org.decembrist.domain.content.functions.HiderOrderFunc
import org.decembrist.domain.content.members.Method
import org.decembrist.generators.IGenerator
import org.decembrist.generators.nextLine

object MethodInfoGenerator: IGenerator<Method> {

    override fun generate(content: Method): CodeBlock {
        val className = content.name

        val annotationsBlock = CodeBlock.builder()
                .add("MethodInfo(")
                .nextLine()
                .indent()
                .add("getIdentifierBySupplier({")
                .indent()
                .add("val function: ")
                .add("getIdentifierBySupplier(::$className),")
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
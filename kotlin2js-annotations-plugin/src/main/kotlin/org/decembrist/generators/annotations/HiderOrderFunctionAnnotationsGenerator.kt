package org.decembrist.generators.annotations

import com.squareup.kotlinpoet.CodeBlock
import org.decembrist.domain.Attribute
import org.decembrist.domain.content.functions.HiderOrderFunc
import org.decembrist.generators.IGenerator
import org.decembrist.generators.nextLine

object HiderOrderFunctionAnnotationsGenerator: IGenerator<HiderOrderFunc> {

    override fun generate(content: HiderOrderFunc): CodeBlock {
        val funcName = content.name
        val annotationsBlock = CodeBlock.builder()
                .add("functionAnnotations.putAndCheck(")
                .nextLine()
                .indent()
                .add("getIdentifier(::$funcName),")
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
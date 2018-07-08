package org.decembrist.generators.annotations

import com.squareup.kotlinpoet.CodeBlock
import org.decembrist.domain.Attribute
import org.decembrist.domain.content.functions.HiderOrderFunc
import org.decembrist.generators.IGenerator
import org.decembrist.generators.nextLine

object HiderOrderFunctionAnnotationsGenerator : IGenerator<HiderOrderFunc> {

    override fun generate(content: HiderOrderFunc): CodeBlock {
        val funcName = content.name
        val functionAnnotations = CodeBlock.builder()
                .add("functionAnnotations.putAndCheck(")
                .nextLine()
                .indent()
                .add("getIdentifier(::$funcName),")
                .nextLine()
        val annotationsBlock = AnnotationBlockGenerator.generate(content)
        functionAnnotations
                .add(annotationsBlock)
                .nextLine()
                .unindent()
                .add(")")
                .nextLine()
        return functionAnnotations.build()
    }

}
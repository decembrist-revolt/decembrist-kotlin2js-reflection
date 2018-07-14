package org.decembrist.generators.annotations

import com.squareup.kotlinpoet.CodeBlock
import org.decembrist.domain.content.functions.HiderOrderFunc
import org.decembrist.generators.nextLine
import org.decembrist.generators.types.HiderOrderedFunctionTypeGenerator

object HiderOrderFunctionAnnotationsGenerator : AbstractFunctionInfoGenerator<HiderOrderFunc>() {

    override val functionTypeGenerator = HiderOrderedFunctionTypeGenerator()

    override fun generate(content: HiderOrderFunc): CodeBlock {
        val functionAnnotations = CodeBlock.builder()
                .add("functionAnnotations.putAndCheck(")
                .nextLine()
                .indent()
                .add(getFunctionIdintifierBlock(content))
                .add(",")
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

    override fun getFunctionRef(content: HiderOrderFunc) = CodeBlock.of("::${content.name}")

}
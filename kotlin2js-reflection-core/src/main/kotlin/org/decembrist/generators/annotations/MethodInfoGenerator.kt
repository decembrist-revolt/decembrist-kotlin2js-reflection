package org.decembrist.generators.annotations

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeName
import org.decembrist.domain.Attribute
import org.decembrist.domain.content.members.Method
import org.decembrist.generators.IGenerator
import org.decembrist.generators.nextLine
import org.decembrist.generators.types.AbstractFunctionTypeGenerator
import org.decembrist.generators.types.MethodTypeGenerator

class MethodInfoGenerator(val clazz: TypeName): AbstractFunctionInfoGenerator<Method>() {

    override val functionTypeGenerator = MethodTypeGenerator(clazz)

    override fun generate(content: Method): CodeBlock {
        val methodInfoBlocks = CodeBlock.builder()
                .add("%T(", METHOD_INFO_TYPE)
                .nextLine()
                .indent()
                .add(getFunctionIdintifierBlock(content))
                .add(",")
                .nextLine()
        val annotationBlock = AnnotationBlockGenerator.generate(content)
        return methodInfoBlocks
                .add(annotationBlock)
                .nextLine()
                .unindent()
                .add(")")
                .build()
    }

    override fun getFunctionRef(content: Method) = CodeBlock.of("%T::${content.name}", clazz)

    companion object {

        val METHOD_INFO_TYPE = ClassName("org.decembrist.model", "MethodInfo")
    }

}
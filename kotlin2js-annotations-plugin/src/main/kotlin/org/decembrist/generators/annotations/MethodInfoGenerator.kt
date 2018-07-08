package org.decembrist.generators.annotations

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeName
import org.decembrist.domain.Attribute
import org.decembrist.domain.content.members.Method
import org.decembrist.generators.IGenerator
import org.decembrist.generators.nextLine
import org.decembrist.generators.types.MethodTypeGenerator

class MethodInfoGenerator(val clazz: TypeName): IGenerator<Method> {

    private val methodTypeGenerator = MethodTypeGenerator(clazz)

    override fun generate(content: Method): CodeBlock {
        val className = content.name
        val methodInfoBlocks = CodeBlock.builder()
                .add("%T(", METHOD_INFO_TYPE)
                .nextLine()
                .indent()
                .add("%T {", GET_IDENTIFIER_BY_SUPPLIER_FUNC_TYPE)
                .indent()
                .add(" val function: ")
                .add(methodTypeGenerator.generate(content))
                .add(" = %T::${content.name}", clazz)
                .nextLine()
                .add("function ")
                .unindent()
                .add("},")
                .nextLine()
        val annotationBlock = AnnotationBlockGenerator.generate(content)
        return methodInfoBlocks
                .add(annotationBlock)
                .nextLine()
                .unindent()
                .add(")")
                .build()
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

    companion object {

        val METHOD_INFO_TYPE = ClassName("org.decembrist.model", "MethodInfo")
        val GET_IDENTIFIER_BY_SUPPLIER_FUNC_TYPE =
                ClassName("org.decembrist.utils", "getIdentifierBySupplier")
    }

}
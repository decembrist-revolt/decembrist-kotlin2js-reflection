package org.decembrist.generators.annotations

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import org.decembrist.domain.content.functions.AbstractFunction
import org.decembrist.generators.IGenerator
import org.decembrist.generators.types.AbstractFunctionTypeGenerator

abstract class AbstractFunctionInfoGenerator<T: AbstractFunction>: IGenerator<T> {

    protected abstract val functionTypeGenerator: AbstractFunctionTypeGenerator<T>

    fun getFunctionIdintifierBlock(content: T) = CodeBlock.builder()
                .add("%T<", GET_IDENTIFIER_BY_SUPPLIER_FUNC_TYPE)
                .add(functionTypeGenerator.generate(content))
                .add(">(")
                .add(getFunctionRef(content))
                .add(")")
                .build()

    protected abstract fun getFunctionRef(content: T): CodeBlock

    companion object {

        val GET_IDENTIFIER_BY_SUPPLIER_FUNC_TYPE =
                ClassName("org.decembrist.utils", "getIdentifierBySupplier")
    }

}
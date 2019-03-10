package org.decembrist.generators.types

import com.squareup.kotlinpoet.*
import org.decembrist.domain.content.functions.AbstractFunction
import org.decembrist.domain.content.members.Method
import org.decembrist.generators.IGenerator
import org.decembrist.services.typesuggestions.TypeSuggestion
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

abstract class AbstractFunctionTypeGenerator<T: AbstractFunction>: IGenerator<T> {

    override fun generate(content: T): CodeBlock {
        val paramsCount = getParamsCount(content)
        val kFunctionType = ClassName(
                KFUNCTION_PACKAGE_NAME,
                "$KFUNCTION_TYPE_NAME$paramsCount"
        )
        val typeNames = getTypeNames(content)
        val functionTypeName = kFunctionType.parameterizedBy(*typeNames)
        return CodeBlock.of("%T", functionTypeName)
    }

    protected open fun getParamsCount(content: T) = content.functionParameters.size

    protected abstract fun getTypeNames(content: T): Array<TypeName>

    protected companion object {

        const val KFUNCTION_TYPE_NAME = "KFunction"
        const val KFUNCTION_PACKAGE_NAME = "kotlin.reflect"

    }


}
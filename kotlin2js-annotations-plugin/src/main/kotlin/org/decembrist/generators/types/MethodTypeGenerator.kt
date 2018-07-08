package org.decembrist.generators.types

import com.squareup.kotlinpoet.*
import org.decembrist.domain.content.members.Method
import org.decembrist.generators.IGenerator
import org.decembrist.services.typesuggestions.TypeSuggestion

class MethodTypeGenerator(val clazz: TypeName): IGenerator<Method> {

    override fun generate(content: Method): CodeBlock {
        val functionParameters = content.functionParameters
        val paramsCount = functionParameters.size + 1
        val kFunctionType = ClassName(
                KFUNCTION_PACKAGE_NAME,
                "$KFUNCTION_TYPE_NAME$paramsCount"
        )
        val returnTypeName = content.returnType.toClassName()
        val parameterTypeNames = functionParameters
                .map { it.type }
                .map(TypeSuggestion::toTypeName)
                .toTypedArray()
        val typeNames = arrayOf(clazz, *parameterTypeNames, returnTypeName)
        val functionTypeName = ParameterizedTypeName.get(kFunctionType, *typeNames)
        return CodeBlock.of("%T", functionTypeName)
    }

    companion object {

        const val KFUNCTION_TYPE_NAME = "KFunction"
        const val KFUNCTION_PACKAGE_NAME = "kotlin.reflect"

    }

}
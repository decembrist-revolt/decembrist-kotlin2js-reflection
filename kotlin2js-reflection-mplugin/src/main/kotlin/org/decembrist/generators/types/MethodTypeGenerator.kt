package org.decembrist.generators.types

import com.squareup.kotlinpoet.*
import org.decembrist.domain.content.members.Method
import org.decembrist.services.typesuggestions.TypeSuggestion

class MethodTypeGenerator(val clazz: TypeName): AbstractFunctionTypeGenerator<Method>() {

    override fun getParamsCount(content: Method): Int = content.functionParameters.size.inc()

    override fun getTypeNames(content: Method): Array<TypeName> {
        val functionParameters = content.functionParameters
        val returnTypeName = content.returnType.toTypeName()
        val parameterTypeNames = functionParameters
                .map { it.type }
                .map(TypeSuggestion::toTypeName)
                .toTypedArray()
        return arrayOf(clazz, *parameterTypeNames, returnTypeName)
    }

}
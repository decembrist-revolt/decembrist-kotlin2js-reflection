package org.decembrist.generators.types

import com.squareup.kotlinpoet.*
import org.decembrist.domain.content.functions.HiderOrderFunc
import org.decembrist.domain.content.members.Method
import org.decembrist.services.typesuggestions.TypeSuggestion

class HiderOrderedFunctionTypeGenerator: AbstractFunctionTypeGenerator<HiderOrderFunc>() {

    override fun getTypeNames(content: HiderOrderFunc): Array<TypeName> {
        val functionParameters = content.functionParameters
        val returnTypeName = content.returnType.toTypeName()
        val parameterTypeNames = functionParameters
                .map { it.type }
                .map(TypeSuggestion::toTypeName)
                .toTypedArray()
        return arrayOf(*parameterTypeNames, returnTypeName)
    }

}
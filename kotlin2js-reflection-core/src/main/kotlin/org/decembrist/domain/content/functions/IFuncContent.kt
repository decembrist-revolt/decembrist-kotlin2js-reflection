package org.decembrist.domain.content.functions

import org.decembrist.domain.content.classes.IEntityContent
import org.decembrist.domain.modifiers.FunctionModifiers
import org.decembrist.services.typesuggestions.TypeSuggestion

interface IFuncContent: IEntityContent {

    var functionParameters: List<FunctionParameter>

    val functionModifiers: FunctionModifiers

    var returnType: TypeSuggestion

    fun isExternal(): Boolean

    fun isInfix(): Boolean

    fun isInline(): Boolean

    fun isOperator(): Boolean

    fun isSuspend(): Boolean

}
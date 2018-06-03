package org.decembrist.domain.content.functions

import org.decembrist.domain.content.classes.IEntityContent
import org.decembrist.domain.modifiers.FunctionModifiers
import org.decembrist.services.TypeSuggestion

interface IFuncContent: IEntityContent {

    val functionParameters: List<FunctionParameter>

    val functionModifiers: FunctionModifiers

    val returnType: TypeSuggestion

    fun isExternal(): Boolean

    fun isInfix(): Boolean

    fun isInline(): Boolean

    fun isOperator(): Boolean

    fun isSuspend(): Boolean

}
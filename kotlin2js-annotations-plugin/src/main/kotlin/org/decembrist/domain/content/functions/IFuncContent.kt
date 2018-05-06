package org.decembrist.domain.content.functions

import org.decembrist.domain.content.classes.IEntityContent
import org.decembrist.domain.modifiers.FunctionModifiers

interface IFuncContent: IEntityContent {

    val functionModifiers: FunctionModifiers

    fun isExternal(): Boolean

    fun isInfix(): Boolean

    fun isInline(): Boolean

    fun isOperator(): Boolean

    fun isSuspend(): Boolean

}
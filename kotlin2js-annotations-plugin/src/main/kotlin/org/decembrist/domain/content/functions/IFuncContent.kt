package org.decembrist.domain.content.functions

import org.decembrist.domain.content.classes.IEntityContent

interface IFuncContent: IEntityContent {

    val isExternal: Boolean

    val isInfix: Boolean

    val isInline: Boolean

    val isOperator: Boolean

    val isSuspend: Boolean

}
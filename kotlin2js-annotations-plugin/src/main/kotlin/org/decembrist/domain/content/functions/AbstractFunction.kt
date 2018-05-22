package org.decembrist.domain.content.functions

import org.decembrist.domain.headers.annotations.AnnotationInstance
import org.decembrist.domain.modifiers.FunctionModifiers

abstract class AbstractFunction(override val name: String,
                                override val functionModifiers: FunctionModifiers,
                                override val functionParameters: List<FunctionParameter>) : IFuncContent {

    constructor(name: String,
                isAbstract: Boolean,
                isFinal: Boolean,
                isOpen: Boolean,
                isExternal: Boolean,
                isInfix: Boolean,
                isInline: Boolean,
                isOperator: Boolean,
                isSuspend: Boolean,
                functionParameters: List<FunctionParameter>)
            : this(name, FunctionModifiers(
            isAbstract,
            isFinal,
            isOpen,
            isExternal,
            isInfix,
            isInline,
            isOperator,
            isSuspend), functionParameters)

    override val annotations: MutableSet<AnnotationInstance> = mutableSetOf()

    override fun isAbstract() = functionModifiers.isAbstract!!

    override fun isFinal() = functionModifiers.isFinal!!

    override fun isOpen() = functionModifiers.isOpen!!

    override fun isExternal() = functionModifiers.isExternal!!

    override fun isInfix() = functionModifiers.isInfix!!

    override fun isInline() = functionModifiers.isInline!!

    override fun isOperator() = functionModifiers.isOperator!!

    override fun isSuspend() = functionModifiers.isSuspend!!

}
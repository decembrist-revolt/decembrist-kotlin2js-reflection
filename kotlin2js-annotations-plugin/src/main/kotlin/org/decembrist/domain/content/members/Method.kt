package org.decembrist.domain.content.members

import org.decembrist.domain.content.functions.AbstractFunction
import org.decembrist.domain.modifiers.FunctionModifiers

class Method(name: String,
             functionModifiers: FunctionModifiers)
    : AbstractFunction(name, functionModifiers), IMember {

    constructor(
            name: String,
            isAbstract: Boolean,
            isFinal: Boolean,
            isOpen: Boolean,
            isExternal: Boolean,
            isInfix: Boolean,
            isInline: Boolean,
            isOperator: Boolean,
            isSuspend: Boolean)
            : this(name, FunctionModifiers(
            isAbstract,
            isFinal,
            isOpen,
            isExternal,
            isInfix,
            isInline,
            isOperator,
            isSuspend))

}
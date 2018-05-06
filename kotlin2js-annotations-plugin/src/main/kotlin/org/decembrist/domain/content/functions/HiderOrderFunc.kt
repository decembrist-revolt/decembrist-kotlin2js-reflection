package org.decembrist.domain.content.functions

import org.decembrist.domain.modifiers.FunctionModifiers

class HiderOrderFunc(name: String,
                     functionModifiers: FunctionModifiers)
    : AbstractFunction(name, functionModifiers) {

    constructor(name: String,
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

package org.decembrist.domain.modifiers

class FunctionModifiers(isAbstract: Boolean? = null,
                        isFinal: Boolean? = null,
                        isOpen: Boolean? = null,
                        val isExternal: Boolean? = null,
                        val isInfix: Boolean? = null,
                        val isInline: Boolean? = null,
                        val isOperator: Boolean? = null,
                        val isSuspend: Boolean? = null)
    : BaseModifiers(
        isAbstract,
        isFinal,
        isOpen)
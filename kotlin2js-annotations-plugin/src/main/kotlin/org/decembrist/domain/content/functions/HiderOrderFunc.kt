package org.decembrist.domain.content.functions

import org.decembrist.domain.modifiers.FunctionModifiers
import org.decembrist.services.typesuggestions.TypeSuggestion

class HiderOrderFunc(name: String,
                     functionModifiers: FunctionModifiers,
                     functionParameters: List<FunctionParameter>,
                     returnType: TypeSuggestion)
    : AbstractFunction(name, functionModifiers, functionParameters, returnType) {

    constructor(name: String,
                isAbstract: Boolean,
                isFinal: Boolean,
                isOpen: Boolean,
                isExternal: Boolean,
                isInfix: Boolean,
                isInline: Boolean,
                isOperator: Boolean,
                isSuspend: Boolean,
                functionParameters: List<FunctionParameter>,
                returnType: TypeSuggestion)
            : this(name, FunctionModifiers(
            isAbstract,
            isFinal,
            isOpen,
            isExternal,
            isInfix,
            isInline,
            isOperator,
            isSuspend),
            functionParameters,
            returnType
    )

}

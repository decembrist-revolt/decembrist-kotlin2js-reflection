package org.decembrist.domain.content.functions

import org.decembrist.domain.modifiers.FunctionModifiers
import org.decembrist.services.typesuggestions.TypeSuggestion
import kotlin.reflect.KVisibility

class HiderOrderFunc(name: String,
                     functionModifiers: FunctionModifiers,
                     functionParameters: List<FunctionParameter>,
                     returnType: TypeSuggestion,
                     visibility: KVisibility)
    : AbstractFunction(name, functionModifiers, functionParameters, returnType, visibility) {

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
                returnType: TypeSuggestion,
                visibility: KVisibility)
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
            returnType,
            visibility
    )

}

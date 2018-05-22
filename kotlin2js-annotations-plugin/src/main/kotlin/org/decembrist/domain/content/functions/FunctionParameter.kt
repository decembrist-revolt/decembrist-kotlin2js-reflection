package org.decembrist.domain.content.functions

import org.decembrist.Message.quotesOnBlank
import org.decembrist.services.TypeSuggestion

class FunctionParameter(val name: String,
                        val type: TypeSuggestion,
                        val defaultValue: Any?) {

    override fun toString(): String {
        val dftValStr = if (defaultValue != null) {
            " = ${quotesOnBlank(defaultValue.toString())}"
        } else ""
        return "$name: $type$dftValStr"
    }

}
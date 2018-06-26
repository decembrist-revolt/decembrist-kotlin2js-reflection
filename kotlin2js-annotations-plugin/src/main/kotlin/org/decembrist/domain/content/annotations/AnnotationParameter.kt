package org.decembrist.domain.content.annotations

import org.decembrist.Message.quotesOnBlank
import org.decembrist.services.typesuggestions.TypeSuggestion

class AnnotationParameter(val name: String,
                          val type: TypeSuggestion,
                          val defaultValue: Any?) {

    override fun toString(): String {
        val dftValStr = if (defaultValue != null) {
            " = ${quotesOnBlank(defaultValue.toString())}"
        } else ""
        return "$name: $type$dftValStr"
    }

}
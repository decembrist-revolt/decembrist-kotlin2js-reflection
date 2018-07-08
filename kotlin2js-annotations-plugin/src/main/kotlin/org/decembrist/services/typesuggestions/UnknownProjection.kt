package org.decembrist.services.typesuggestions

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import org.decembrist.services.typesuggestions.TypeSuggestion.AbstractProjection
import org.decembrist.services.typesuggestions.exceptions.UnknownTypeConversionException

class UnknownProjection(type: String,
                        nullable: Boolean = false,
                        `in`: Boolean = false,
                        out: Boolean = false
) : AbstractProjection(type, nullable, `in`, out) {

    override fun toTypeName(): TypeName {
        throw throw UnknownTypeConversionException()
    }

    override fun toClassName(): ClassName {
        throw throw UnknownTypeConversionException()
    }
}
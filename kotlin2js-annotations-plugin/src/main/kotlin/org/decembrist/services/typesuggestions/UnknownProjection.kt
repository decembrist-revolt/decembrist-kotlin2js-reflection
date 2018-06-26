package org.decembrist.services.typesuggestions

import com.squareup.kotlinpoet.TypeName

class UnknownProjection(type: String,
                        nullable: Boolean = false,
                        `in`: Boolean = false,
                        out: Boolean = false,
                        projections: List<TypeSuggestion> = emptyList()
) : TypeSuggestion.AbstractProjection(type, nullable, `in`, out, projections) {

    override fun toTypeName(): TypeName {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
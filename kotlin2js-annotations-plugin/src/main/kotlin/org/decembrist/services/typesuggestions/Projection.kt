package org.decembrist.services.typesuggestions

import com.squareup.kotlinpoet.TypeName

class Projection(type: String,
                 nullable: Boolean = false,
                 packageName: String = "",
                 `in`: Boolean = false,
                 out: Boolean = false,
                 projections: List<TypeSuggestion> = emptyList()
) : TypeSuggestion.AbstractProjection(type, nullable, `in`, out, projections) {

    override fun toTypeName(): TypeName {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
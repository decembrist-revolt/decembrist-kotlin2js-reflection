package org.decembrist.services.typesuggestions

import com.squareup.kotlinpoet.ClassName
import org.decembrist.services.typesuggestions.TypeSuggestion.AbstractProjection

class Projection(type: String,
                 nullable: Boolean = false,
                 val packageName: String = "",
                 `in`: Boolean = false,
                 out: Boolean = false
) : AbstractProjection(type, nullable, `in`, out) {

    override fun toClassName() = ClassName(packageName, type)

}
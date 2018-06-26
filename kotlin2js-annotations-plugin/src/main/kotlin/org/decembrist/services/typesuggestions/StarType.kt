package org.decembrist.services.typesuggestions

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.WildcardTypeName

class StarType : TypeSuggestion.Type("*") {

    override fun toTypeName(): TypeName = WildcardTypeName.subtypeOf(Any::class)

}
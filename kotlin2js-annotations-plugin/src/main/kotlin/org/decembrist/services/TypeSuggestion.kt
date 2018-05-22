package org.decembrist.services

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.WildcardTypeName
import org.decembrist.Message.concatenateClassName

sealed class TypeSuggestion(val type: String,
                            val projections: MutableList<TypeSuggestion>) {

    class Unknown(type: String,
                  projections: MutableList<TypeSuggestion> = mutableListOf()) : TypeSuggestion(type, projections) {

        override fun toTypeName(): TypeName {
            throw UnsupportedOperationException("This is unknown type, that cant be converted")
        }

    }

    open class Type(type: String,
                    val packageName: String = "",
                    projections: MutableList<TypeSuggestion> = mutableListOf()) : TypeSuggestion(type, projections) {

        override fun toTypeName(): TypeName = ClassName(packageName, type)

    }

    class StarType: Type("*") {

        override fun toTypeName(): TypeName = WildcardTypeName.subtypeOf(Any::class)

    }

    abstract fun toTypeName(): TypeName

    override fun toString(): String {
        return if (this is Type) {
            concatenateClassName(type, packageName)
        } else type
    }

}
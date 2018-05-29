package org.decembrist.services

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.WildcardTypeName
import org.decembrist.Message.concatenateClassName

sealed class TypeSuggestion(val type: String,
                            val projections: MutableList<TypeSuggestion>,
                            val nullable: Boolean = false) {

    class Unknown(type: String,
                  projections: MutableList<TypeSuggestion> = mutableListOf(),
                  nullable: Boolean = false) : TypeSuggestion(type, projections, nullable) {

        override fun toTypeName(): TypeName {
            throw UnsupportedOperationException("This is unknown type, that cant be converted")
        }

    }

    open class Type(type: String,
                    val packageName: String = "",
                    projections: MutableList<TypeSuggestion> = mutableListOf(),
                    nullable: Boolean = false) : TypeSuggestion(type, projections, nullable) {

        override fun toTypeName(): TypeName = ClassName(packageName, type)

    }

    class StarType : Type("*") {

        override fun toTypeName(): TypeName = WildcardTypeName.subtypeOf(Any::class)

    }

    class Projection(type: String,
                     packageName: String = "",
                     projections: MutableList<TypeSuggestion> = mutableListOf(),
                     nullable: Boolean = false,
                     val `in`: Boolean = false,
                     val out: Boolean = false) : Type(type, packageName, projections, nullable)

    abstract fun toTypeName(): TypeName

    override fun toString(): String {
        return if (this is Type) {
            concatenateClassName(type, packageName)
        } else type
    }

}
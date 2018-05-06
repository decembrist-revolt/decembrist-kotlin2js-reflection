package org.decembrist.services

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import org.decembrist.Message.concatenateClassName

sealed class TypeSuggestion(val type: String) {

    class Unknown(type: String) : TypeSuggestion(type) {

        override fun toTypeName(): TypeName {
            throw UnsupportedOperationException("This is unknown type, that cant be converted")
        }

    }

    class Type(type: String, val packageName: String = "") : TypeSuggestion(type) {

        override fun toTypeName(): TypeName {
            return ClassName(packageName, type)
        }

    }

    abstract fun toTypeName(): TypeName

    override fun toString(): String {
        return if (this is Type) {
            concatenateClassName(type, packageName)
        } else type
    }

}
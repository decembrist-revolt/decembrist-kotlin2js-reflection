package org.decembrist.services.typesuggestions

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import org.decembrist.Message.concatenateClassName

sealed class TypeSuggestion(val type: String,
                            val nullable: Boolean = false) {

    class Unknown(type: String,
                  nullable: Boolean = false) : TypeSuggestion(type, nullable) {

        override fun toTypeName(): TypeName {
            throw UnsupportedOperationException("This is unknown type, that cant be converted")
        }

    }

    open class Type(type: String,
                    val packageName: String = "",
                    nullable: Boolean = false) : TypeSuggestion(type, nullable) {

        override fun toTypeName(): TypeName = ClassName(packageName, type)

    }

    abstract class AbstractProjection(type: String,
                                      nullable: Boolean = false,
                                      val `in`: Boolean = false,
                                      val out: Boolean = false,
                                      var projections: List<TypeSuggestion> = emptyList()
    ) : TypeSuggestion(type, nullable)

    abstract fun toTypeName(): TypeName

    override fun toString(): String {
        return if (this is Type) {
            concatenateClassName(type, packageName)
        } else type
    }

}
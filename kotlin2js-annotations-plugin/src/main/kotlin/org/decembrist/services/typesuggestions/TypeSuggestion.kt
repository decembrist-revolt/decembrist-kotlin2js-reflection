package org.decembrist.services.typesuggestions

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.WildcardTypeName
import org.decembrist.Message.concatenateClassName
import org.decembrist.services.typesuggestions.exceptions.UnknownTypeConversionException

sealed class TypeSuggestion(val type: String,
                            val nullable: Boolean = false) {

    class Unknown(type: String,
                  nullable: Boolean = false) : TypeSuggestion(type, nullable) {

        override fun toClassName(): ClassName {
            throw UnknownTypeConversionException()
        }

        override fun toTypeName(): TypeName {
            throw UnknownTypeConversionException()
        }
    }

    open class Type(type: String,
                    val packageName: String = "",
                    nullable: Boolean = false) : TypeSuggestion(type, nullable) {

        override fun toClassName(): ClassName = ClassName(packageName, type)

        override fun toTypeName(): TypeName = toClassName().asNullable(nullable)
    }

    abstract class AbstractProjection(type: String,
                                      nullable: Boolean = false,
                                      val `in`: Boolean = false,
                                      val out: Boolean = false
    ) : TypeSuggestion(type, nullable) {

        override fun toTypeName(): TypeName {
            val className = toClassName()
            return (if (`in` or out) {
                if (`in`) {
                    WildcardTypeName.supertypeOf(className)
                } else {
                    WildcardTypeName.subtypeOf(className)
                }
            } else className).asNullable(nullable)
        }

    }

    open class ProjectionContainer(val typeSuggestion: TypeSuggestion,
                                   var projections: List<TypeSuggestion> = emptyList()
    ): TypeSuggestion(typeSuggestion.type, typeSuggestion.nullable) {

        override fun toClassName() = typeSuggestion.toClassName()

        override fun toTypeName(): TypeName {
            val className = typeSuggestion.toClassName()
            val projectionTypes = projections
                    .map(TypeSuggestion::toTypeName)
                    .toTypedArray()
            return ParameterizedTypeName.get(className, *projectionTypes).asNullable(nullable)
        }

    }

    abstract fun toClassName(): ClassName

    abstract fun toTypeName(): TypeName

    fun toProjectionContainer(projections: List<TypeSuggestion> = emptyList()) = ProjectionContainer(
            this,
            projections
    )

    fun TypeName.asNullable(nullable: Boolean = false) = if (nullable) {
        this.asNullable()
    } else this

    override fun toString(): String {
        return if (this is Type) {
            concatenateClassName(type, packageName)
        } else type
    }

}
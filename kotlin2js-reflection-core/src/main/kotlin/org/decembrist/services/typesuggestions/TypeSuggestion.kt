package org.decembrist.services.typesuggestions

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.WildcardTypeName
import org.decembrist.Message.concatenateClassName
import org.decembrist.services.typesuggestions.exceptions.UnknownTypeConversionException
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

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

        override fun toString() = "Unknown(${super.toString()})"
    }

    open class Type(type: String,
                    val packageName: String = "",
                    nullable: Boolean = false) : TypeSuggestion(type, nullable) {

        override fun toClassName(): ClassName = ClassName(packageName, type)

        override fun toTypeName(): TypeName = toClassName().asNullable(nullable)

        override fun toString() = "Type(${super.toString()})"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Type) return false
            if (!super.equals(other)) return false

            if (packageName != other.packageName) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + packageName.hashCode()
            return result
        }


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
                    WildcardTypeName.consumerOf(className)
                } else {
                    WildcardTypeName.producerOf(className)
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
            return className.parameterizedBy(*projectionTypes).asNullable(nullable)
        }

        override fun toString(): String {
            val typeString = typeSuggestion.toString()
            return "$typeString${projections.joinToString(prefix = "<", postfix = ">")}"
        }

    }

    abstract fun toClassName(): ClassName

    abstract fun toTypeName(): TypeName

    fun toProjectionContainer(projections: List<TypeSuggestion> = emptyList()) = ProjectionContainer(
            this,
            projections
    )

    fun TypeName.asNullable(nullable: Boolean = false): TypeName = if (nullable) {
        this.asNullable()
    } else this

    override fun toString(): String {
        return if (this is Type) {
            concatenateClassName(type, packageName)
        } else type
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (this::class.isInstance(other).not()) return false

        other as TypeSuggestion
        if (type != other.type) return false
        if (nullable != other.nullable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + nullable.hashCode()
        return result
    }


}
package org.decembrist.services

import com.github.sarahbuisson.kotlinparser.KotlinParser.ValueArgumentContext
import org.decembrist.domain.Attribute
import org.decembrist.domain.content.annotations.AnnotationClass
import org.decembrist.domain.content.annotations.AnnotationParameter
import org.decembrist.domain.headers.annotations.AnnotationInstance
import org.decembrist.services.typesuggestions.TypeSuggestion

object AnnotationService {

    val embededJsAnnotations = setOf(
            getEmbeddedJsAnnotationClass(
                    "JsName",
                    listOf(getEmbeddedJsAnnotationParameter("name"))
            ),
            getEmbeddedJsAnnotationClass(
                    "native",
                    listOf(getEmbeddedJsAnnotationParameter("name", ""))
            ),
            getEmbeddedJsAnnotationClass("nativeGetter"),
            getEmbeddedJsAnnotationClass("nativeSetter"),
            getEmbeddedJsAnnotationClass("nativeInvoke"),
            getEmbeddedJsAnnotationClass(
                    "JsModule",
                    listOf(getEmbeddedJsAnnotationParameter("import"))
            ),
            getEmbeddedJsAnnotationClass("JsNonModule"),
            getEmbeddedJsAnnotationClass(
                    "JsQualifier",
                    listOf(getEmbeddedJsAnnotationParameter("import"))
            )
    )

    private const val UNKNOWN_ATTRIBUTE_NAME = ""

    fun retrieveAttribute(ctx: ValueArgumentContext): Attribute {
        val name = if (ctx.ASSIGNMENT() != null) {
            ctx.simpleIdentifier().text
        } else UNKNOWN_ATTRIBUTE_NAME
        val expression = ctx.expression().text
        val (type, value) = if (expression.containsAny("\"", "\'")) {
            defineTypeByExpression(expression) to expression
        } else TypeSuggestion.Unknown("") to expression
        val resultValue = checkMult(ctx, value)
        return Attribute(name, resultValue, type)
    }

    fun hasUnknownAttributesInfo(annotationInst: AnnotationInstance): Boolean {
        return annotationInst.attributes
                .any { it.type is TypeSuggestion.Unknown || it.name == UNKNOWN_ATTRIBUTE_NAME }
    }

    fun isAttributeWithUnknownName(attribute: Attribute) = attribute.name == UNKNOWN_ATTRIBUTE_NAME

    fun isAttributeWithUnknownType(attribute: Attribute) = attribute.type is TypeSuggestion.Unknown

    private fun checkMult(ctx: ValueArgumentContext, expression: String) =
            if (ctx.MULT() != null) {
                "*$expression"
            } else {
                expression
            }

    private fun defineTypeByExpression(expression: String): TypeSuggestion {
        return when {
            expression.startsWith("\"") -> TypeSuggestion.Type("String", "kotlin")
            expression.startsWith("\'") -> TypeSuggestion.Type("Char", "kotlin")
            else -> TypeSuggestion.Unknown("")
        }
    }

    private fun String.containsAny(vararg string: String) = string.any { this.contains(it) }

    private fun getEmbeddedJsAnnotationClass(className: String,
                                             parameters: List<AnnotationParameter> = emptyList())
            : AnnotationClass {

        return AnnotationClass(
                className,
                false,
                false,
                false,
                false,
                false,
                false).apply {
            this.parameters += parameters
        }
    }

    private fun getEmbeddedJsAnnotationParameter(
            paramName: String, defaultValue: String? = null): AnnotationParameter {
        return AnnotationParameter(
                paramName,
                TypeSuggestion.Type("String"),
                defaultValue)
    }

}
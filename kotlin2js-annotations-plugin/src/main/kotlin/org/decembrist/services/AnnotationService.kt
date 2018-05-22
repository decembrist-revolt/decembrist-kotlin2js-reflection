package org.decembrist.services

import com.github.sarahbuisson.kotlinparser.KotlinParser
import com.github.sarahbuisson.kotlinparser.KotlinParser.*
import org.decembrist.domain.Attribute
import org.decembrist.domain.Import
import org.decembrist.domain.content.annotations.AnnotationClass
import org.decembrist.domain.content.annotations.AnnotationParameter
import org.decembrist.domain.content.functions.FunctionParameter
import org.decembrist.domain.headers.annotations.AnnotationInstance
import org.decembrist.services.ImportService.findFullClass
import org.decembrist.services.TypeService.getTypeSuggestion

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
        val (type, value) = if (expression.contains("\"")) {
            TypeSuggestion.Type("String") to expression.replace("\"", "")
        } else TypeSuggestion.Unknown("") to expression
        return Attribute(name, value, type)
    }

    /**
     * TODO default value
     */
    fun retrieveParameter(ctx: ClassParameterContext,
                          imports: Collection<Import>): AnnotationParameter {
        val name = ctx.simpleIdentifier().text
        val className = ctx.type().text
        val fullClass = findFullClass(imports, className)
        val type = getTypeSuggestion(fullClass)
        return AnnotationParameter(name, type, null)
    }

    fun hasUnknownAttributesInfo(annotationInst: AnnotationInstance): Boolean {
        return annotationInst.attributes
                .any { it.type is TypeSuggestion.Unknown || it.name == UNKNOWN_ATTRIBUTE_NAME }
    }

    fun isAttributeWithUnknownName(attribute: Attribute) = attribute.name == UNKNOWN_ATTRIBUTE_NAME

    fun isAttributeWithUnknownType(attribute: Attribute) = attribute.type is TypeSuggestion.Unknown

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
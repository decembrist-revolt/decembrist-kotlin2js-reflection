package org.decembrist.services

import com.github.sarahbuisson.kotlinparser.KotlinParser.ClassParameterContext
import com.github.sarahbuisson.kotlinparser.KotlinParser.ValueArgumentContext
import com.squareup.kotlinpoet.ClassName
import org.decembrist.domain.Attribute
import org.decembrist.domain.Import
import org.decembrist.domain.content.annotations.AnnotationParameter
import org.decembrist.services.ImportService.findFullClass

object AnnotationService {

    fun retrieveAttribute(ctx: ValueArgumentContext): Attribute {
        val name = if (ctx.ASSIGNMENT() != null) ctx.simpleIdentifier().text else ""
        val expression = ctx.expression().text
        val (type, value) = if (expression.contains("\"")) {
            ClassName("", "String") to expression.replace("\"", "")
        } else null to expression
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
        val type = ClassName("", fullClass)
        return AnnotationParameter(name, type, null)
    }

}
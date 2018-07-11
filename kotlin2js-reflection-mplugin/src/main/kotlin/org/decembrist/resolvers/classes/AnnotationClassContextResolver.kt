package org.decembrist.resolvers.classes

import com.github.sarahbuisson.kotlinparser.KotlinParser.ClassDeclarationContext
import com.github.sarahbuisson.kotlinparser.KotlinParser.PrimaryConstructorContext
import org.decembrist.domain.Import
import org.decembrist.domain.content.annotations.AnnotationClass
import org.decembrist.domain.content.annotations.AnnotationParameter
import org.decembrist.services.AnnotationService.retrieveParameter
import org.decembrist.services.RuleContextService.getClassModifiers
import org.decembrist.services.RuleContextService.getClassName

class AnnotationClassContextResolver(private val imports: Collection<Import>)
    : AbstractClassContextResolver<AnnotationClass> {

    override fun resolve(ctx: ClassDeclarationContext): AnnotationClass {
        val className = getClassName(ctx)
        val classModifiers = getClassModifiers(ctx)
        val primaryConstructor = ctx.payload
                ?.let { it as ClassDeclarationContext }?.primaryConstructor()
        return AnnotationClass(className, classModifiers).apply {
            parameters += retrieveParameters(primaryConstructor)
        }
    }

    private fun retrieveParameters(
            primaryConstructor: PrimaryConstructorContext?): List<AnnotationParameter> {
        return primaryConstructor
                ?.classParameters()
                ?.classParameter()
                ?.map { retrieveParameter(it, imports) } ?: emptyList()
    }

}
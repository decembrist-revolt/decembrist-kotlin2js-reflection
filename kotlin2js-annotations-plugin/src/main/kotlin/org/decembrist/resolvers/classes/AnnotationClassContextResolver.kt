package org.decembrist.resolvers.classes

import com.github.sarahbuisson.kotlinparser.KotlinParser.ClassDeclarationContext
import com.github.sarahbuisson.kotlinparser.KotlinParser.PrimaryConstructorContext
import org.decembrist.domain.Import
import org.decembrist.domain.content.annotations.AnnotationClass
import org.decembrist.domain.content.annotations.AnnotationParameter
import org.decembrist.domain.content.classes.Class
import org.decembrist.services.AnnotationService.retrieveParameter

class AnnotationClassContextResolver(
        private val imports: Collection<Import>) : ClassContextResolver() {

    override fun resolve(ctx: ClassDeclarationContext): Class {
        val clazz = super.resolve(ctx)
        val primaryConstructor = ctx.payload
                ?.let { it as ClassDeclarationContext }?.primaryConstructor()
        return AnnotationClass(clazz).apply {
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
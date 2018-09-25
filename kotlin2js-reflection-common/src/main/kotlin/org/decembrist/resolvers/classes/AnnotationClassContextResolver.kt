package org.decembrist.resolvers.classes

import org.decembrist.parser.KotlinParser.ClassDeclarationContext
import org.decembrist.domain.Import
import org.decembrist.domain.content.annotations.AnnotationClass
import org.decembrist.services.RuleContextService.getClassModifiers
import org.decembrist.services.RuleContextService.getClassName
import org.decembrist.services.RuleContextService.getVisibility
import org.decembrist.services.RuleContextService.retrieveParameters

class AnnotationClassContextResolver(private val imports: Collection<Import>)
    : AbstractClassContextResolver<AnnotationClass> {

    override fun resolve(ctx: ClassDeclarationContext): AnnotationClass {
        val className = getClassName(ctx)
        val classModifiers = getClassModifiers(ctx)
        val primaryConstructor = ctx.payload
                ?.let { it as ClassDeclarationContext }?.primaryConstructor()
        val visibility = getVisibility(ctx.modifierList())
        return AnnotationClass(className, classModifiers, visibility).apply {
            parameters += retrieveParameters(primaryConstructor, imports)
        }
    }

}
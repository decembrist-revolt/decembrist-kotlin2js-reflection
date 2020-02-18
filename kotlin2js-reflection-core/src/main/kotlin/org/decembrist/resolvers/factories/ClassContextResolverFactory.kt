package org.decembrist.resolvers.factories

import org.decembrist.domain.Import
import org.decembrist.domain.content.classes.AbstractClass
import org.decembrist.parser.KotlinParser.ClassDeclarationContext
import org.decembrist.resolvers.classes.AbstractClassContextResolver
import org.decembrist.resolvers.classes.AnnotationClassContextResolver
import org.decembrist.resolvers.classes.ClassContextResolver
import org.decembrist.services.Modifier.ANNOTATION_MODIFIER
import org.decembrist.services.RuleContextService.modifierExists

class ClassContextResolverFactory private constructor(private val imports: Collection<Import>)
    : IContextResolverFactory<ClassDeclarationContext, AbstractClassContextResolver<out AbstractClass>> {

    override fun getResolver(
            ctx: ClassDeclarationContext): AbstractClassContextResolver<out AbstractClass> {
        val modifiers = ctx.modifiers()?.modifier()
        val isAnnotation = modifierExists(modifiers, ANNOTATION_MODIFIER)
        return if (isAnnotation) {
            AnnotationClassContextResolver(imports)
        } else ClassContextResolver()
    }

    companion object {

        fun createInstance(imports: Collection<Import>) = ClassContextResolverFactory(imports)

    }

}
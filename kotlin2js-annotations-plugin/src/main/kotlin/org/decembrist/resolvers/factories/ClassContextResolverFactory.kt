package org.decembrist.resolvers.factories

import com.github.sarahbuisson.kotlinparser.KotlinParser.ClassDeclarationContext
import org.decembrist.domain.Import
import org.decembrist.resolvers.classes.AnnotationClassContextResolver
import org.decembrist.resolvers.classes.ClassContextResolver
import org.decembrist.services.Modifier.ANNOTATION_MODIFIER
import org.decembrist.services.RuleContextService.modifierExists

class ClassContextResolverFactory private constructor(private val imports: Collection<Import>)
    : IContextResolverFactory<ClassDeclarationContext, ClassContextResolver> {

    override fun getResolver(
            ctx: ClassDeclarationContext): ClassContextResolver {
        val modifiers = ctx.modifierList()?.modifier()
        val isAnnotation = modifierExists(modifiers, ANNOTATION_MODIFIER)
        return if (isAnnotation) AnnotationClassContextResolver(imports) else ClassContextResolver()
    }

    companion object {

        fun createInstance(imports: Collection<Import>) = ClassContextResolverFactory(imports)

    }

}
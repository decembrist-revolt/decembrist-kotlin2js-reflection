package org.decembrist.resolvers.classes

import com.github.sarahbuisson.kotlinparser.KotlinParser.ClassDeclarationContext
import org.decembrist.domain.content.classes.Class
import org.decembrist.resolvers.IContextResolver
import org.decembrist.services.Modifier.*
import org.decembrist.services.RuleContextService.getEntityModifiers
import org.decembrist.services.RuleContextService.modifierExists

open class ClassContextResolver : IContextResolver<ClassDeclarationContext, Class> {

    override fun resolve(ctx: ClassDeclarationContext): Class {
        val className = ctx.simpleIdentifier().text
        val modifiers = ctx.modifierList()?.modifier()
        val (isAbstract, isFinal, isOpen) = getEntityModifiers(modifiers)
        val isAnnotation = modifierExists(modifiers, ANNOTATION_MODIFIER)
        val isData = modifierExists(modifiers, DATA_MODIFIER)
        val isInner = modifierExists(modifiers, INNER_MODIFIER)
        val isSealed = modifierExists(modifiers, SEALED_MODIFIER)
        return Class(className, isAbstract, isFinal, isOpen, isData, isInner, isSealed)
    }

}
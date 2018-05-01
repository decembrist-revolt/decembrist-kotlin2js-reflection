package org.decembrist.services

import com.github.sarahbuisson.kotlinparser.KotlinParser.*
import org.decembrist.domain.Import
import org.decembrist.resolvers.AnnotationInstanceContextResolver
import org.decembrist.services.Modifier.*

object RuleContextService {

    fun getAnnotations(ctx: FunctionDeclarationContext,
                       imports: Collection<Import>) = ctx.modifierList()
            ?.annotations()
            .orEmpty()
            .map { it.annotation() }
            .let { AnnotationInstanceContextResolver.fromList(it, imports) }

    fun getAnnotations(ctx: ClassDeclarationContext,
                       imports: Collection<Import>) = ctx.modifierList()
            ?.annotations()
            .orEmpty()
            .map { it.annotation() }
            .let { AnnotationInstanceContextResolver.fromList(it, imports) }

    fun modifierExists(modifiers: MutableList<ModifierContext>?,
                       modifier: Modifier): Boolean {
        return modifiers?.any { it.text == modifier.modifierName } == true
    }

    /**
     * @return triple (isAbstract, isFinal, isOpen) modifiers
     */
    fun getEntityModifiers(
            modifiers: MutableList<ModifierContext>?): Triple<Boolean, Boolean, Boolean> {
        return if (modifiers == null) {
            Triple(false, false, false)
        } else {
            val isAbstract = modifierExists(modifiers, ABSTRACT_MODIFIER)
            val isFinal = modifierExists(modifiers, FINAL_MODIFIER)
            val isOpen = modifierExists(modifiers, OPEN_MODIFIER)
            Triple(isAbstract, isFinal, isOpen)
        }
    }

}
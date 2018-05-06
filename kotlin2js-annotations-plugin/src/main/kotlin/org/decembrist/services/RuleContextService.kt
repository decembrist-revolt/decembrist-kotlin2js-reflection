package org.decembrist.services

import com.github.sarahbuisson.kotlinparser.KotlinParser.*
import org.decembrist.domain.Import
import org.decembrist.domain.modifiers.ClassModifiers
import org.decembrist.domain.modifiers.FunctionModifiers
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

    fun getClassModifiers(ctx: ClassDeclarationContext): ClassModifiers {
        val modifiers = ctx.modifierList()?.modifier()
        val (isAbstract, isFinal, isOpen) = getEntityModifiers(modifiers)
        val isData = modifierExists(modifiers, DATA_MODIFIER)
        val isInner = modifierExists(modifiers, INNER_MODIFIER)
        val isSealed = modifierExists(modifiers, SEALED_MODIFIER)
        return ClassModifiers(
                isAbstract,
                isFinal,
                isOpen,
                isData,
                isInner,
                isSealed
        )
    }

    fun getFunctionModifiers(ctx: FunctionDeclarationContext): FunctionModifiers {
        val modifiers = ctx.modifierList()?.modifier()
        val (isAbstract, isFinal, isOpen) = getEntityModifiers(modifiers)
        val isExternal = modifierExists(modifiers, EXTERNAL_MODIFIER)
        val isInfix = modifierExists(modifiers, INNER_MODIFIER)
        val isInline = modifierExists(modifiers, INLINE_MODIFIER)
        val isOperator = modifierExists(modifiers, OPERATOR_MODIFIER)
        val isSuspend = modifierExists(modifiers, SUSPEND_MODIFIER)
        return FunctionModifiers(
                isAbstract,
                isFinal,
                isOpen,
                isExternal,
                isInfix,
                isInline,
                isOperator,
                isSuspend
        )
    }

    fun getClassName(ctx: ClassDeclarationContext) = ctx.simpleIdentifier().text

    fun getMemberOwnerClassName(ctx: ClassMemberDeclarationContext) = ctx.parent
            .parent
            .let { it as ClassDeclarationContext }
            .let { getClassName(it) }

    /**
     * @return triple (isAbstract, isFinal, isOpen) modifiers
     */
    private fun getEntityModifiers(
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
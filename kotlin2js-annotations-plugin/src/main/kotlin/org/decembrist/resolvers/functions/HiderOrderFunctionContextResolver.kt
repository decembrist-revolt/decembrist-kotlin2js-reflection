package org.decembrist.resolvers.functions

import com.github.sarahbuisson.kotlinparser.KotlinParser.FunctionDeclarationContext
import org.decembrist.domain.content.functions.HiderOrderFunc
import org.decembrist.services.Modifier.*
import org.decembrist.services.RuleContextService.getEntityModifiers
import org.decembrist.services.RuleContextService.modifierExists

class HiderOrderFunctionContextResolver(
        funcName: String) : AbstractFunctionContextResolver<HiderOrderFunc>(funcName) {

    override fun resolve(ctx: FunctionDeclarationContext): HiderOrderFunc {
        val modifiers = ctx.modifierList()?.modifier()
        val (isAbstract, isFinal, isOpen) = getEntityModifiers(modifiers)
        val isExternal = modifierExists(modifiers, EXTERNAL_MODIFIER)
        val isInfix = modifierExists(modifiers, INNER_MODIFIER)
        val isInline = modifierExists(modifiers, INLINE_MODIFIER)
        val isOperator = modifierExists(modifiers, OPERATOR_MODIFIER)
        val isSuspend = modifierExists(modifiers, SUSPEND_MODIFIER)
        return HiderOrderFunc(funcName, isAbstract, isFinal, isOpen, isExternal, isInfix, isInline,
                isOperator, isSuspend)
    }

}
package org.decembrist.resolvers.functions

import org.decembrist.domain.Import
import org.decembrist.domain.content.functions.HiderOrderFunc
import org.decembrist.parser.KotlinParser.FunctionDeclarationContext
import org.decembrist.services.RuleContextService.getFunctionModifiers
import org.decembrist.services.RuleContextService.getVisibility
import org.decembrist.services.RuleContextService.retrieveFunctionParameters
import org.decembrist.services.RuleContextService.retrieveFunctionReturnType

class HiderOrderFunctionContextResolver(
        funcName: String,
        val imports: Collection<Import>) : AbstractFunctionContextResolver<HiderOrderFunc>(funcName) {

    override fun resolve(ctx: FunctionDeclarationContext): HiderOrderFunc {
        val functionModifiers = getFunctionModifiers(ctx)
        val functionParameters = retrieveFunctionParameters(ctx, imports)
                .toMutableList()
        val returnType = retrieveFunctionReturnType(ctx, imports)
        val visibility = getVisibility(ctx.modifiers())
        return HiderOrderFunc(
                funcName,
                functionModifiers,
                functionParameters,
                returnType,
                visibility
        )
    }

}
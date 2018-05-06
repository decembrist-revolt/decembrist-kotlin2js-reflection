package org.decembrist.resolvers.functions

import com.github.sarahbuisson.kotlinparser.KotlinParser.FunctionDeclarationContext
import org.decembrist.domain.content.functions.HiderOrderFunc
import org.decembrist.services.RuleContextService.getFunctionModifiers

class HiderOrderFunctionContextResolver(
        funcName: String) : AbstractFunctionContextResolver<HiderOrderFunc>(funcName) {

    override fun resolve(ctx: FunctionDeclarationContext): HiderOrderFunc {
        val functionModifiers = getFunctionModifiers(ctx)
        return HiderOrderFunc(funcName, functionModifiers)
    }

}
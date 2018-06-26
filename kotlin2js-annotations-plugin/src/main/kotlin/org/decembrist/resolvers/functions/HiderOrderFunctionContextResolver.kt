package org.decembrist.resolvers.functions

import com.github.sarahbuisson.kotlinparser.KotlinParser.FunctionDeclarationContext
import org.decembrist.domain.Import
import org.decembrist.domain.content.functions.HiderOrderFunc
import org.decembrist.services.RuleContextService.getFunctionModifiers
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
        return HiderOrderFunc(funcName, functionModifiers, functionParameters, returnType)
    }

}
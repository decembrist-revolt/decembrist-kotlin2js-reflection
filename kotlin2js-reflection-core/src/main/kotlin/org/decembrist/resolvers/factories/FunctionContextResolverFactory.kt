package org.decembrist.resolvers.factories

import org.decembrist.domain.Import
import org.decembrist.domain.content.functions.IFuncContent
import org.decembrist.parser.KotlinParser.FunctionDeclarationContext
import org.decembrist.resolvers.functions.AbstractFunctionContextResolver
import org.decembrist.resolvers.functions.HiderOrderFunctionContextResolver

class FunctionContextResolverFactory(val imports: Collection<Import>)
    : IContextResolverFactory<FunctionDeclarationContext, AbstractFunctionContextResolver<out IFuncContent>> {

    override fun getResolver(
            ctx: FunctionDeclarationContext): AbstractFunctionContextResolver<out IFuncContent> {
        val identifier = ctx.simpleIdentifier()
        val funcName = if (identifier != null) {
            identifier.text
        } else throw UnsupportedOperationException()
        return HiderOrderFunctionContextResolver(funcName, imports)
    }

}
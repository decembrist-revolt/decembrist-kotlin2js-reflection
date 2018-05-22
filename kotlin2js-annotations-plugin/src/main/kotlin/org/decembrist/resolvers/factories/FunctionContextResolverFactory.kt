package org.decembrist.resolvers.factories

import com.github.sarahbuisson.kotlinparser.KotlinParser.FunctionDeclarationContext
import org.decembrist.domain.Import
import org.decembrist.domain.content.functions.IFuncContent
import org.decembrist.resolvers.functions.AbstractFunctionContextResolver
import org.decembrist.resolvers.functions.HiderOrderFunctionContextResolver

class FunctionContextResolverFactory(val imports: Collection<Import>)
    : IContextResolverFactory<FunctionDeclarationContext, AbstractFunctionContextResolver<out IFuncContent>> {

    override fun getResolver(
            ctx: FunctionDeclarationContext): AbstractFunctionContextResolver<out IFuncContent> {
        val identifier = ctx.identifier()
        val funcName = if (identifier != null) {
            identifier.text
        } else throw UnsupportedOperationException()
        return HiderOrderFunctionContextResolver(funcName, imports)
    }

}
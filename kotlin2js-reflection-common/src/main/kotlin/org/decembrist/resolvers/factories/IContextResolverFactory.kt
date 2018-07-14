package org.decembrist.resolvers.factories

import org.antlr.v4.runtime.RuleContext
import org.decembrist.domain.content.IContent
import org.decembrist.resolvers.IContextResolver

interface IContextResolverFactory<T : RuleContext, D : IContextResolver<T, out IContent>> {

    fun getResolver(ctx: T): D

}
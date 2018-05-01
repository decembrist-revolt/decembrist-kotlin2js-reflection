package org.decembrist.resolvers

import org.antlr.v4.runtime.RuleContext
import org.decembrist.domain.content.IContent

interface IContextResolver<T : RuleContext, D : IContent> {

    fun resolve(ctx: T): D

}
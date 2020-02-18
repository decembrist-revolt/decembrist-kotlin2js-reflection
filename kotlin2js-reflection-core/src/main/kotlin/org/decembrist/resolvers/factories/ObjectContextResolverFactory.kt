package org.decembrist.resolvers.factories

import org.decembrist.domain.Import
import org.decembrist.domain.content.classes.AbstractClass
import org.decembrist.parser.KotlinParser
import org.decembrist.resolvers.classes.AbstractObjectContextResolver
import org.decembrist.resolvers.classes.ObjectContextResolver

class ObjectContextResolverFactory private constructor(private val imports: Collection<Import>)
    : IContextResolverFactory<KotlinParser.ObjectDeclarationContext, AbstractObjectContextResolver<out AbstractClass>> {

    override fun getResolver(
            ctx: KotlinParser.ObjectDeclarationContext): AbstractObjectContextResolver<out AbstractClass> {
        return ObjectContextResolver()
    }

    companion object {

        fun createInstance(imports: Collection<Import>) = ObjectContextResolverFactory(imports)

    }

}
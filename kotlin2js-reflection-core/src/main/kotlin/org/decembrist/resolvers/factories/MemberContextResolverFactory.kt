package org.decembrist.resolvers.factories

import org.decembrist.domain.Import
import org.decembrist.domain.content.members.IMember
import org.decembrist.parser.KotlinParser.ClassMemberDeclarationContext
import org.decembrist.resolvers.members.AbstractMemberContextResolver
import org.decembrist.resolvers.members.MethodContextResolver

class MemberContextResolverFactory private constructor(private val imports: Collection<Import>)
    : IContextResolverFactory<ClassMemberDeclarationContext, AbstractMemberContextResolver<out IMember>> {

    override fun getResolver(
            ctx: ClassMemberDeclarationContext): AbstractMemberContextResolver<out IMember> {
        return when {
            ctx.declaration()?.functionDeclaration()?.simpleIdentifier() != null ->
                MethodContextResolver(imports)
            else -> throw UnsupportedOperationException()
        }
    }

    companion object {

        fun createInstance(imports: Collection<Import>) = MemberContextResolverFactory(imports)

    }

}
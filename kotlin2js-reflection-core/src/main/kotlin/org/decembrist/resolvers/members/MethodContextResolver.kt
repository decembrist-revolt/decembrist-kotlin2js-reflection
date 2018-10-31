package org.decembrist.resolvers.members

import org.decembrist.parser.KotlinParser.ClassMemberDeclarationContext
import org.decembrist.domain.Import
import org.decembrist.domain.content.members.Method
import org.decembrist.services.RuleContextService.getAnnotations
import org.decembrist.services.RuleContextService.getFunctionModifiers
import org.decembrist.services.RuleContextService.getVisibility
import org.decembrist.services.RuleContextService.retrieveFunctionParameters
import org.decembrist.services.RuleContextService.retrieveFunctionReturnType
import kotlin.reflect.KVisibility

class MethodContextResolver(
        private val imports: Collection<Import>) : AbstractMemberContextResolver<Method> {

    override fun resolve(ctx: ClassMemberDeclarationContext): Method {
        val funcContext = ctx.functionDeclaration()
        val annotations = getAnnotations(funcContext, imports)
        val functionParameters = retrieveFunctionParameters(funcContext, imports)
                .toMutableList()
        val returnType = retrieveFunctionReturnType(funcContext, imports)
        val name = funcContext.identifier().text
        val functionModifiers = getFunctionModifiers(funcContext)
        val visibility = getVisibility(funcContext.modifierList())
        return Method(
                name,
                functionModifiers,
                functionParameters,
                returnType,
                visibility
        ).apply {
            this.annotations += annotations
        }
    }

}
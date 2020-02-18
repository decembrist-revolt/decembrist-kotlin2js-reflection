package org.decembrist.resolvers.classes

import org.decembrist.domain.content.classes.Class
import org.decembrist.parser.KotlinParser
import org.decembrist.services.RuleContextService.getClassName
import org.decembrist.services.RuleContextService.getModifiers
import org.decembrist.services.RuleContextService.getVisibility

open class ObjectContextResolver : AbstractObjectContextResolver<Class> {

    override fun resolve(ctx: KotlinParser.ObjectDeclarationContext): Class {
        val className = getClassName(ctx)
        val classModifiers = getModifiers(ctx.modifiers())
        val visibility = getVisibility(ctx.modifiers())
        return Class(className, classModifiers, visibility)
    }

}
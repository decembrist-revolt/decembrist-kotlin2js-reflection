package org.decembrist.resolvers.classes

import com.github.sarahbuisson.kotlinparser.KotlinParser.ClassDeclarationContext
import org.decembrist.domain.content.classes.Class
import org.decembrist.services.RuleContextService.getClassModifiers
import org.decembrist.services.RuleContextService.getClassName
import org.decembrist.services.RuleContextService.getVisibility

open class ClassContextResolver : AbstractClassContextResolver<Class> {

    override fun resolve(ctx: ClassDeclarationContext): Class {
        val className = getClassName(ctx)
        val classModifiers = getClassModifiers(ctx)
        val visibility = getVisibility(ctx.modifierList())
        return Class(className, classModifiers, visibility)
    }

}
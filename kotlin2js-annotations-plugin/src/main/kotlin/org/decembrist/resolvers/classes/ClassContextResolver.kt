package org.decembrist.resolvers.classes

import com.github.sarahbuisson.kotlinparser.KotlinParser.ClassDeclarationContext
import org.decembrist.domain.content.classes.Class
import org.decembrist.services.RuleContextService.getClassModifiers
import org.decembrist.services.RuleContextService.getClassName

open class ClassContextResolver : AbstractClassContextResolver<Class> {

    override fun resolve(ctx: ClassDeclarationContext): Class {
        val className = getClassName(ctx)
        val classModifiers = getClassModifiers(ctx)
        return Class(className, classModifiers)
    }

}
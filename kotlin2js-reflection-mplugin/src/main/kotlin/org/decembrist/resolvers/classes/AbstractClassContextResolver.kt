package org.decembrist.resolvers.classes

import com.github.sarahbuisson.kotlinparser.KotlinParser.ClassDeclarationContext
import org.decembrist.domain.content.classes.AbstractClass
import org.decembrist.resolvers.IContextResolver

interface AbstractClassContextResolver<T: AbstractClass>
    : IContextResolver<ClassDeclarationContext, T>
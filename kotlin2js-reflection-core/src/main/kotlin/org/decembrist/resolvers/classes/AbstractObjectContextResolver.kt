package org.decembrist.resolvers.classes

import org.decembrist.domain.content.classes.AbstractClass
import org.decembrist.parser.KotlinParser
import org.decembrist.resolvers.IContextResolver

interface AbstractObjectContextResolver<T: AbstractClass>
    : IContextResolver<KotlinParser.ObjectDeclarationContext, T>
package org.decembrist.resolvers.members

import org.decembrist.parser.KotlinParser.ClassMemberDeclarationContext
import org.antlr.v4.runtime.ParserRuleContext
import org.decembrist.domain.content.classes.Class
import org.decembrist.domain.content.members.IMember
import org.decembrist.domain.content.members.Method
import org.decembrist.resolvers.IContextResolver

interface AbstractMemberContextResolver<T : IMember>
    : IContextResolver<ClassMemberDeclarationContext, T>
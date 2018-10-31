package org.decembrist.resolvers.functions

import org.decembrist.parser.KotlinParser.FunctionDeclarationContext
import org.decembrist.domain.content.functions.IFuncContent
import org.decembrist.resolvers.IContextResolver

abstract class AbstractFunctionContextResolver<T: IFuncContent>(
        protected val funcName: String) : IContextResolver<FunctionDeclarationContext, T>
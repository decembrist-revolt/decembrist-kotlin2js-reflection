package org.decembrist.services.typecontexts

import org.decembrist.parser.KotlinParser.TypeContext
import org.antlr.v4.runtime.ParserRuleContext

abstract class CustomType(typeContext: ParserRuleContext) : TypeContext(typeContext, 0)
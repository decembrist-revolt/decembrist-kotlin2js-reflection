package org.decembrist.services.typecontexts

import com.github.sarahbuisson.kotlinparser.KotlinParser.TypeContext
import org.antlr.v4.runtime.ParserRuleContext

abstract class CustomType(typeContext: ParserRuleContext) : TypeContext(typeContext, 0)
package org.decembrist.services.typecontexts

import org.decembrist.parser.KotlinParser
import org.antlr.v4.runtime.ParserRuleContext

class StarType(typeContext: ParserRuleContext) : CustomType(typeContext) {

    override fun typeReference(): KotlinParser.TypeReferenceContext? = null

    override fun getText() = "*"

}
package org.decembrist.parsers

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ErrorNodeImpl
import org.decembrist.parser.KotlinParserBaseListener

open class ParserLibFixListener : KotlinParserBaseListener() {

    /**
     * Fix class.toString as class recognition issue
     * https://github.com/sarahBuisson/kotlin-parser/issues/3
     */
    fun classValidation(ctx: ParserRuleContext): Boolean {
        return ctx.children
                .orEmpty()
                .any { it is ErrorNodeImpl }
                .not()
    }

}
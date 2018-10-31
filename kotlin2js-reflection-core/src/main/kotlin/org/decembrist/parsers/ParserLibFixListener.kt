package org.decembrist.parsers

import org.decembrist.parser.KotlinParser
import org.decembrist.parser.KotlinParser.ClassDeclarationContext
import org.decembrist.parser.KotlinParserBaseListener
import org.antlr.v4.runtime.tree.ErrorNodeImpl

open class ParserLibFixListener : KotlinParserBaseListener() {

    /**
     * Fix class.toString as class recognition issue
     * https://github.com/sarahBuisson/kotlin-parser/issues/3
     */
    fun classValidation(ctx: ClassDeclarationContext): Boolean {
        return ctx.children
                .orEmpty()
                .any { it is ErrorNodeImpl }
                .not()
    }

}
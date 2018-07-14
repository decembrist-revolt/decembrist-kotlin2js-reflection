package org.decembrist.parsers

import com.github.sarahbuisson.kotlinparser.KotlinParser
import com.github.sarahbuisson.kotlinparser.KotlinParser.ClassDeclarationContext
import com.github.sarahbuisson.kotlinparser.KotlinParserBaseListener
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
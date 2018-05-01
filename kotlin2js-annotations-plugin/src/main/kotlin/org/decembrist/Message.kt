package org.decembrist

import com.github.sarahbuisson.kotlinparser.KotlinParser.FunctionDeclarationContext
import org.antlr.v4.runtime.RuleContext

object Message {

    val lineSeparator = System.getProperty("line.separator")

    fun folderExistenceFailedMessage(folderPath: String) = "$folderPath must exist and be folder"

    fun contextInfo(ctx: RuleContext) = ctx.text

    fun unsupportedAnnotatedType(ctx: RuleContext, fileName: String): String {
        val wrongAnnotatedType = WrongAnnotatedType(ctx, fileName)
        return "Annotation unsupported yet for $wrongAnnotatedType"
    }

    private class WrongAnnotatedType(val ctx: RuleContext, val fileName: String) {

        override fun toString(): String {
            val annotatedEntity = when (ctx) {
                is FunctionDeclarationContext -> ctx.text
                        .removePrefix(ctx.modifierList().text)
                        .removePrefix("fun")
                        .replace(lineSeparator, "")
                else -> ctx.text
            }
            val annotations = when (ctx) {
                is FunctionDeclarationContext -> ctx.modifierList()
                        .annotations()
                        .joinToString { it.annotation().text }
                else -> ""
            }
            return """|
                |entity = {
                |   "annotations": "$annotations",
                |   "entity": "$annotatedEntity",
                |   "file": "$fileName"
                |}
            """.trimMargin()
        }

    }

}
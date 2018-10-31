package org.decembrist

import org.decembrist.parser.KotlinParser.FunctionDeclarationContext
import org.antlr.v4.runtime.RuleContext
import java.io.File

internal object Message {

    val lineSeparator = System.getProperty("line.separator")

    val pathSeparator = File.separatorChar

    fun folderExistenceFailedMessage(folderPath: String) = "$folderPath must exist and be folder"

    fun contextInfo(ctx: RuleContext) = ctx.text

    fun unsupportedAnnotatedType(ctx: RuleContext, fileName: String): String {
        val wrongAnnotatedType = WrongAnnotatedType(ctx, fileName)
        return "Annotation unsupported yet for $wrongAnnotatedType"
    }

    fun concatenateClassName(className: String, packageName: String? = null): String {
        return if (packageName != null && packageName.isNotBlank()) {
            "$packageName.$className"
        } else className
    }

    fun quotesOnBlank(value: String) = if (value.isBlank()) "\"\"" else value

    fun mainClassNotFoundMessage(mainClass: String) = "$mainClass not found in classpath"

    fun entityIsPrivate(entityName: String) = "$entityName is private and won't be processed"

    fun classIsPrivate(entityName: String) = "Class $entityName is private and won't be processed"

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
package org.decembrist.resolvers

import org.decembrist.parser.KotlinParser
import org.decembrist.parser.KotlinParser.ImportHeaderContext
import org.decembrist.parser.KotlinParser.ImportListContext
import org.decembrist.domain.Import
import org.decembrist.domain.content.ImportsContent

class ImportContextResolver : IContextResolver<ImportListContext, ImportsContent> {

    override fun resolve(ctx: ImportListContext) = ctx.importHeader()
            .map { importContext ->
                val importClass = importContext.identifier().text
                val aliasName = resolveAlias(importContext)
                return@map Import(importClass, aliasName)
            }.let { ImportsContent(it) }

    private fun resolveAlias(importContext: ImportHeaderContext) = importContext
            .importAlias()
            ?.simpleIdentifier()
            ?.text

}

package org.decembrist.resolvers

import com.github.sarahbuisson.kotlinparser.KotlinParser.ImportListContext
import org.decembrist.domain.Import
import org.decembrist.domain.content.ImportsContent

class ImportContextResolver : IContextResolver<ImportListContext, ImportsContent> {

    override fun resolve(ctx: ImportListContext) = ctx.importHeader()
            .map { Import(it.identifier().text) }
            .let { ImportsContent(it) }

}

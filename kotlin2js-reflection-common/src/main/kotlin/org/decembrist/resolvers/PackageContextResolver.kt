package org.decembrist.resolvers

import org.decembrist.parser.KotlinParser.PackageHeaderContext
import org.decembrist.domain.content.Package

class PackageContextResolver : IContextResolver<PackageHeaderContext, Package> {

    override fun resolve(ctx: PackageHeaderContext) = if (ctx.identifier() != null) {
        Package(ctx.identifier().text)
    } else {
        Package("")
    }

}
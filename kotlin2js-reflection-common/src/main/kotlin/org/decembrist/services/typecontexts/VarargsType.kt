package org.decembrist.services.typecontexts

import org.decembrist.parser.KotlinParser
import org.decembrist.parser.KotlinParser.TypeContext

class VarargsType(val ctx: TypeContext) {

    companion object {

        fun of(ctx: TypeContext) = VarargsType(ctx)

    }

}
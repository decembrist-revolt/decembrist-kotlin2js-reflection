package org.decembrist.services.typecontexts

import com.github.sarahbuisson.kotlinparser.KotlinParser
import com.github.sarahbuisson.kotlinparser.KotlinParser.TypeContext

class VarargsType(val ctx: TypeContext) {

    companion object {

        fun of(ctx: TypeContext) = VarargsType(ctx)

    }

}
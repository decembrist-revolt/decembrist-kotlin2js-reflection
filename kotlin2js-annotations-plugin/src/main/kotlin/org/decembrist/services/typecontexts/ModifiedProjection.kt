package org.decembrist.services.typecontexts

import com.github.sarahbuisson.kotlinparser.KotlinParser
import com.github.sarahbuisson.kotlinparser.KotlinParser.TypeProjectionContext
import com.github.sarahbuisson.kotlinparser.KotlinParser.TypeReferenceContext
import org.antlr.v4.runtime.ParserRuleContext

class ModifiedProjection(val projection: TypeProjectionContext,
                         typeContext: ParserRuleContext): CustomType(typeContext) {

    val isIN: Boolean
    val isOUT: Boolean

    override fun typeReference(): TypeReferenceContext? = projection.type().typeReference()

    override fun getText() = with(projection) {
        val type = type().typeReference() ?: type().nullableType().typeReference()
        type.text
    }

    init {
        val varianceAnnotations = projection
                .typeProjectionModifierList()
                .varianceAnnotation()
        var isIN = false
        var isOUT = false
        for (varianceAnnotation in varianceAnnotations) {
            isOUT = isOUT || varianceAnnotation.OUT() != null
            isIN = isIN || varianceAnnotation.IN() != null
        }
        this.isIN = isIN
        this.isOUT = isOUT
    }

}
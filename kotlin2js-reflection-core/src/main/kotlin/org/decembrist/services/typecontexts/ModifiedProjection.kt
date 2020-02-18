package org.decembrist.services.typecontexts

import org.antlr.v4.runtime.ParserRuleContext
import org.decembrist.parser.KotlinParser
import org.decembrist.parser.KotlinParser.TypeProjectionContext
import org.decembrist.parser.KotlinParser.TypeReferenceContext

class ModifiedProjection(val projection: TypeProjectionContext,
                         typeContext: ParserRuleContext): CustomType(typeContext) {

    val isIN: Boolean
    val isOUT: Boolean

    override fun typeReference(): TypeReferenceContext? = projection.type().typeReference()

    override fun nullableType(): KotlinParser.NullableTypeContext? = projection
            .type()
            .nullableType()

    override fun getText() = with(projection) {
        val type = type()
                .typeReference() ?: type()
                .nullableType()
                .typeReference()
        type.text
    }

    init {
        val varianceAnnotations = projection
                .typeProjectionModifiers()
                .typeProjectionModifier()
                .map { it.varianceModifier() }
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
package org.decembrist.services

import org.antlr.v4.runtime.RuleContext
import org.decembrist.Message.unsupportedAnnotatedType

object ExceptionService {

    fun throwUnsupportedAnnotatedTypeException(ctx: RuleContext, fileName: String) {
        throw UnsupportedOperationException(unsupportedAnnotatedType(ctx, fileName))
    }

}
package org.decembrist.domain.content.functions

import org.decembrist.domain.headers.annotations.AnnotationInstance

class HiderOrderFunc(override val name: String,
                     override val isAbstract: Boolean,
                     override val isFinal: Boolean,
                     override val isOpen: Boolean,
                     override val isExternal: Boolean,
                     override val isInfix: Boolean,
                     override val isInline: Boolean,
                     override val isOperator: Boolean,
                     override val isSuspend: Boolean) : IFuncContent {

    override val annotations: MutableSet<AnnotationInstance> = mutableSetOf()

}
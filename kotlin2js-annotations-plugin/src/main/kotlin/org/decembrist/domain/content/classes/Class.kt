package org.decembrist.domain.content.classes

import org.decembrist.domain.headers.annotations.AnnotationInstance

open class Class(override val name: String,
            override val isAbstract: Boolean,
            override val isFinal: Boolean,
            override val isOpen: Boolean,
            override val isData: Boolean,
            override val isInner: Boolean,
            override val isSealed: Boolean): IClassContent {

    override val annotations: MutableSet<AnnotationInstance> = mutableSetOf()

}


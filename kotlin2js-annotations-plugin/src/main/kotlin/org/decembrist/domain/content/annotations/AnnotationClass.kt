package org.decembrist.domain.content.annotations

import org.decembrist.domain.content.classes.AbstractClass
import org.decembrist.domain.modifiers.ClassModifiers

class AnnotationClass(name: String,
                      classModifiers: ClassModifiers): AbstractClass(name, classModifiers) {

    constructor(name: String,
                isAbstract: Boolean,
                isFinal: Boolean,
                isOpen: Boolean,
                isData: Boolean,
                isInner: Boolean,
                isSealed: Boolean)
            : this(name, ClassModifiers(
            isAbstract,
            isFinal,
            isOpen,
            isData,
            isInner,
            isSealed))

    val parameters: MutableList<AnnotationParameter> = mutableListOf()

}
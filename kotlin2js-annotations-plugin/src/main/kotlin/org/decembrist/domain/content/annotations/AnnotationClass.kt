package org.decembrist.domain.content.annotations

import org.decembrist.domain.content.classes.Class

class AnnotationClass(clazz: Class)
    : Class(clazz.name, clazz.isAbstract, clazz.isFinal, clazz.isOpen, clazz.isData, clazz.isInner,
        clazz.isSealed) {

    val parameters: MutableList<AnnotationParameter> = mutableListOf()

}
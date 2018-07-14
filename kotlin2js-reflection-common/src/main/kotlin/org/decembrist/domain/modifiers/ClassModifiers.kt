package org.decembrist.domain.modifiers

class ClassModifiers(isAbstract: Boolean? = null,
                     isFinal: Boolean? = null,
                     isOpen: Boolean? = null,
                     val isData: Boolean? = null,
                     val isInner: Boolean? = null,
                     val isSealed: Boolean? = null)
    : BaseModifiers(
        isAbstract,
        isFinal,
        isOpen)
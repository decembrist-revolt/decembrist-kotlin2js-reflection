package org.decembrist.domain.content.classes

import org.decembrist.domain.headers.annotations.AnnotationInstance
import org.decembrist.domain.modifiers.ClassModifiers
import kotlin.reflect.KVisibility

abstract class AbstractClass(override val name: String,
                             override val classModifiers: ClassModifiers,
                             override val visibility: KVisibility) : IClassContent {

    constructor(name: String,
                isAbstract: Boolean,
                isFinal: Boolean,
                isOpen: Boolean,
                isData: Boolean,
                isInner: Boolean,
                isSealed: Boolean,
                visibility: KVisibility)
            : this(name, ClassModifiers(
                    isAbstract,
                    isFinal,
                    isOpen,
                    isData,
                    isInner,
                    isSealed), visibility)

    override var annotations: Set<AnnotationInstance> = emptySet()

    override fun isAbstract() = classModifiers.isAbstract!!

    override fun isFinal() = classModifiers.isFinal!!

    override fun isOpen() = classModifiers.isOpen!!

    override fun isData() = classModifiers.isData!!

    override fun isInner() = classModifiers.isInner!!

    override fun isSealed() = classModifiers.isSealed!!

}
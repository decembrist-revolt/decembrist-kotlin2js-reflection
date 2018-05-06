package org.decembrist.domain.content.classes

import org.decembrist.domain.modifiers.ClassModifiers

interface IClassContent : IEntityContent {

    val classModifiers: ClassModifiers

    fun isData(): Boolean

    fun isInner(): Boolean

    fun isSealed(): Boolean

}
package org.decembrist.domain.content.classes

import org.decembrist.domain.content.IContent
import org.decembrist.domain.content.IAnnotated
import org.decembrist.domain.content.IVisibilityModified

interface IEntityContent: IAnnotated, IContent, IVisibilityModified {

    val name: String

    fun isAbstract(): Boolean

    fun isFinal(): Boolean

    fun isOpen(): Boolean

}
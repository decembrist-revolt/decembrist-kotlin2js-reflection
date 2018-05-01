package org.decembrist.domain.content.classes

import org.decembrist.domain.content.IContent
import org.decembrist.domain.content.IAnnotated

interface IEntityContent: IAnnotated, IContent {

    val name: String

    val isAbstract: Boolean

    val isFinal: Boolean

    val isOpen: Boolean

}
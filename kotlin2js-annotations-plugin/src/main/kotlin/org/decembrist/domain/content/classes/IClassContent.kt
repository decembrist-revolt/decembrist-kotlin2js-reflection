package org.decembrist.domain.content.classes

interface IClassContent : IEntityContent {

    val isData: Boolean

    val isInner: Boolean

    val isSealed: Boolean

}
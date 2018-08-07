package org.decembrist.domain.content

import kotlin.reflect.KVisibility

interface IVisibilityModified: IContent {

    val visibility: KVisibility

}
package org.decembrist.domain.headers.annotations

import com.squareup.kotlinpoet.TypeName
import org.decembrist.domain.Attribute
import org.decembrist.domain.content.IContent

class AnnotationInstance(val type: TypeName) : IContent {

    val attributes = mutableListOf<Attribute>()

}
package org.decembrist.domain.headers.annotations

import org.decembrist.domain.Attribute
import org.decembrist.domain.content.IContent
import org.decembrist.services.TypeSuggestion

class AnnotationInstance(val type: TypeSuggestion) : IContent {

    val attributes = mutableListOf<Attribute>()

}
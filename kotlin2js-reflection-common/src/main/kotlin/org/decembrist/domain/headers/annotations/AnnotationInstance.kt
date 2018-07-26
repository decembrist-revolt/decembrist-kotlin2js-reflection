package org.decembrist.domain.headers.annotations

import org.decembrist.domain.Attribute
import org.decembrist.domain.content.IContent
import org.decembrist.services.typesuggestions.TypeSuggestion

class AnnotationInstance(val type: TypeSuggestion) : IContent {

    var attributes = emptyList<Attribute>()

}
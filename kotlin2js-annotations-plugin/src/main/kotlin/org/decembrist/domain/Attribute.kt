package org.decembrist.domain

import org.decembrist.services.TypeSuggestion

data class Attribute(val name: String,
                     val value: String,
                     val type: TypeSuggestion)
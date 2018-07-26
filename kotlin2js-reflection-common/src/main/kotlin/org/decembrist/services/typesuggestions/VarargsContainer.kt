package org.decembrist.services.typesuggestions

import org.decembrist.services.typesuggestions.TypeSuggestion.ProjectionContainer

class VarargsContainer(typeSuggestion: TypeSuggestion,
                       projections: List<TypeSuggestion> = emptyList())
    : ProjectionContainer(typeSuggestion, projections) {

    init {
        assert(projections.size == 1)
    }

    fun asProjectionContainer() = ProjectionContainer(typeSuggestion, projections)

    override fun toString() = "varargs ${projections.first()}"

}
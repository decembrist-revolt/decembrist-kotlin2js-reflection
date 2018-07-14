package org.decembrist.services.typesuggestions

import org.decembrist.services.typesuggestions.TypeSuggestion.ProjectionContainer

class VarargsContainer(typeSuggestion: TypeSuggestion,
                       projections: List<TypeSuggestion> = emptyList())
    : ProjectionContainer(typeSuggestion, projections) {

    fun asProjectionContainer() = ProjectionContainer(typeSuggestion, projections)

}
package org.decembrist.services

import org.decembrist.services.cache.CacheService
import org.decembrist.services.typesuggestions.Projection
import org.decembrist.services.typesuggestions.TypeConstants
import org.decembrist.services.typesuggestions.TypeSuggestion
import org.decembrist.services.typesuggestions.VarargsContainer

object ReplaceTypeService {

    fun replaceUnknownType(type: TypeSuggestion, packageName: String): TypeSuggestion {
        val classItem = if (type is TypeSuggestion.Unknown) {
            CacheService.getClassCache()
                    .filter { classItem -> classItem.packageName == packageName }
                    .firstOrNull { classItem -> classItem.className == type.type }
                    ?: CacheService.ClassItem.of(type.type)
        } else CacheService.ClassItem.of(type.type)

        return when (type) {
            is VarargsContainer -> replaceVarargsContainer(classItem, type, packageName)
            is TypeSuggestion.ProjectionContainer -> replaceProjectionContainer(type, packageName, classItem)
            is TypeSuggestion.AbstractProjection -> Projection(
                    classItem.className,
                    type.nullable,
                    classItem.packageName,
                    type.`in`,
                    type.out
            )
            else -> TypeSuggestion.Type(
                    classItem.className,
                    classItem.packageName,
                    type.nullable
            )
        }
    }

    private fun replaceVarargsContainer(classItem: CacheService.ClassItem,
                                        type: VarargsContainer,
                                        packageName: String): TypeSuggestion {
        assert(type.projections.size == 1)
        val projection = type.projections
                .first()
                .let { replaceUnknownType(it, packageName) }
        val result = if (projection is TypeSuggestion.Type) {
            if (projection.packageName == "" || projection.packageName == "kotlin") {
                TypeConstants
                        .getTypedArrayByType(projection.type)
                        ?.type
            } else null
        } else null

        return result ?: replaceProjectionContainer(
                type.asProjectionContainer(),
                packageName,
                classItem
        )
    }

    private fun replaceProjectionContainer(type: TypeSuggestion.ProjectionContainer,
                                           packageName: String,
                                           classItem: CacheService.ClassItem): TypeSuggestion.ProjectionContainer {
        val projections = type.projections
                .map { replaceUnknownType(it, packageName) }
        return TypeSuggestion.ProjectionContainer(
                TypeSuggestion.Type(
                        classItem.className,
                        classItem.packageName,
                        type.nullable
                ),
                projections
        )
    }

}
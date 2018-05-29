package org.decembrist.services

import com.github.sarahbuisson.kotlinparser.KotlinParser.*
import org.decembrist.domain.Import
import org.decembrist.services.TypeSuggestion.*
import org.decembrist.services.typecontexts.ModifiedProjection
import org.decembrist.services.typecontexts.StarType

object TypeService {

    /**
     * @return connected [Type] or [Unknown] on [className] without package
     */
    fun getTypeSuggestion(className: String, nullable: Boolean = false): TypeSuggestion {
        val packageName = className.substringBeforeLast(".")
        val clazz = className.substringAfterLast(".")
        return if (packageName == className) {
            Unknown(clazz, nullable = nullable)
        } else {
            Type(clazz, packageName, nullable = nullable)
        }
    }

    fun getTypeSuggestion(paramCxt: TypeContext, imports: Collection<Import>): TypeSuggestion {
        val typeReferenceContext =
            paramCxt.typeReference() ?: paramCxt.nullableType()?.typeReference()
        //TODO nullable types
        val typeContext = typeReferenceContext
                ?.userType()
                ?.simpleUserType()
                .orEmpty()
                .firstOrNull()
        return if (typeContext != null) {
            val projections = typeContext.typeArguments()
                    ?.typeProjection()
                    .orEmpty()
            val typeName = typeContext.simpleIdentifier().text
            val result = typeSuggestionFromImports(typeName, imports, paramCxt)
            if (projections.isNotEmpty()) {
                val typeProjections = projections
                        .map { retrieveType(it, typeContext) }
                        .map { getTypeSuggestion(it, imports) }
                result.projections.addAll(typeProjections)
            }
            result
        } else {
            val typeName = paramCxt.text
            typeSuggestionFromImports(typeName, imports, paramCxt)
        }
    }

    private fun typeSuggestionFromImports(typeName: String,
                                          imports: Collection<Import>,
                                          paramCxt: TypeContext): TypeSuggestion {
        return when (typeName) {
            "*" -> TypeSuggestion.StarType()
            else -> {
                val nullable = paramCxt
                        .nullableType()
                        ?.QUEST()
                        ?.isNotEmpty() == true
                val fullClassName = ImportService.findFullClass(imports, typeName)
                when (paramCxt) {
                    is ModifiedProjection -> Projection(
                            fullClassName,
                            nullable = nullable,
                            `in` = paramCxt.isIN,
                            out = paramCxt.isOUT
                    )
                    else -> getTypeSuggestion(fullClassName, nullable)
                }
            }
        }
    }

    private fun retrieveType(projection: TypeProjectionContext,
                             typeContext: SimpleUserTypeContext) = when {
        projection.MULT() != null -> StarType(typeContext)
        projection.typeProjectionModifierList()?.isEmpty?.not() == true -> ModifiedProjection(
                projection,
                typeContext
        )
        else -> projection.type()
    }

}
package org.decembrist.services

import com.github.sarahbuisson.kotlinparser.KotlinParser.*
import org.antlr.v4.runtime.ParserRuleContext
import org.decembrist.domain.Import
import org.decembrist.services.TypeSuggestion.Type
import org.decembrist.services.TypeSuggestion.Unknown

object TypeService {

    /**
     * @return connected [Type] or [Unknown] on [className] without package
     */
    fun getTypeSuggestion(className: String): TypeSuggestion {
        val packageName = className.substringBeforeLast(".")
        val clazz = className.substringAfterLast(".")
        return if (packageName == className) {
            Unknown(clazz)
        } else {
            Type(clazz, packageName)
        }
    }

    fun getTypeSuggestion(paramCxt: TypeContext, imports: Collection<Import>): TypeSuggestion {
        val typeContext = paramCxt
                .typeReference()
                ?.userType()
                ?.simpleUserType()
                .orEmpty()
                .firstOrNull()
        return if (typeContext != null) {
            val projections = typeContext.typeArguments()
                    ?.typeProjection()
                    .orEmpty()
            val typeName = typeContext.simpleIdentifier().text
            val result = typeSuggestionFromImports(typeName, imports)
            if (projections.isNotEmpty()) {
                val typeProjections = projections
                        .map { retrieveType(it, typeContext) }
                        .map { getTypeSuggestion(it, imports) }
                result.projections.addAll(typeProjections)
            }
            result
        } else {
            val typeName = paramCxt.text
            typeSuggestionFromImports(typeName, imports)
        }
    }

    private fun typeSuggestionFromImports(typeName: String,
                                          imports: Collection<Import>): TypeSuggestion {
        return when (typeName) {
            "*" -> TypeSuggestion.StarType()
            else -> {
                val fullClassName = ImportService.findFullClass(imports, typeName)
                getTypeSuggestion(fullClassName)
            }
        }
    }

    private fun retrieveType(projection: TypeProjectionContext,
                             typeContext: SimpleUserTypeContext) = when {
        projection.MULT() != null -> StarType(typeContext, 0)
        else -> projection.type()
    }

    class StarType(typeContext: ParserRuleContext,
                   state: Int) : TypeContext(typeContext, state) {

        override fun typeReference(): TypeReferenceContext? = null

        override fun getText() = "*"

    }

}
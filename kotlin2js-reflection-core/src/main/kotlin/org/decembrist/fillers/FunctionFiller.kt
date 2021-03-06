package org.decembrist.fillers

import org.decembrist.domain.content.KtFileContent
import org.decembrist.domain.content.functions.FunctionParameter
import org.decembrist.domain.content.functions.IFuncContent
import org.decembrist.domain.content.members.IMembered
import org.decembrist.services.ReplaceTypeService.replaceUnknownType
import org.decembrist.services.TypeService.splitFullClassName
import org.decembrist.services.cache.CacheService
import org.decembrist.services.typesuggestions.*
import org.decembrist.services.typesuggestions.TypeSuggestion.*
import java.util.Collections.singletonList

class FunctionFiller private constructor(private val fileContents: Collection<KtFileContent>) {

    /**
     * Fill missing annotation instance info from [fileContent]
     */
    fun fill(): Collection<KtFileContent> {
        for (fileContent in fileContents) {
            fillMethods(fileContent)
            fillFunctions(fileContent)
        }
        return fileContents
    }

    private fun fillFunctions(fileContent: KtFileContent) {
        val functions = fileContent.functions
        fill(fileContent, functions)
    }

    private fun fillMethods(fileContent: KtFileContent) {
        val methods = fileContent.classes
                .filter { it is IMembered }
                .map { (it as IMembered).methods }
                .flatten()
        val unknownTypeFunctions = methods.filter { hasUnknownTypes(it) }
        fill(fileContent, unknownTypeFunctions)
    }

    private fun fill(fileContent: KtFileContent,
                     functions: Collection<IFuncContent>) {
        val packageName = fileContent.`package`?.name ?: ""
        for (function in functions) {
            replaceUnknownParameters(function, packageName)
            replaceUnknownReturnType(function, packageName)
        }
    }

    private fun hasUnknownTypes(function: IFuncContent): Boolean {
        val parameterTypes = function.functionParameters
                .map { it.type }
        return parameterTypes
                .plus(function.returnType)
                .any { hasUnknownTypes(singletonList(it)) }
    }

    private fun hasUnknownTypes(types: List<TypeSuggestion>): Boolean {
        return types.any { type ->
            return@any when (type) {
                is Unknown -> true
                is UnknownProjection -> true
                is ProjectionContainer -> hasUnknownTypes(
                        type.projections.plus(type.typeSuggestion)
                )
                else -> false
            }
        }
    }

    private fun replaceUnknownParameters(function: IFuncContent, packageName: String) {
        val newParams = function.functionParameters
                .map { parameter ->
                    val type = parameter.type
                    val newType = replaceUnknownType(type, packageName)
                    return@map FunctionParameter(
                            parameter.name,
                            newType,
                            parameter.defaultValue
                    )
                }
        function.functionParameters = newParams
    }

    private fun replaceUnknownReturnType(function: IFuncContent, packageName: String) {
        val returnType = function.returnType
        function.returnType = when (returnType) {
            is Unknown -> replaceUnknownType(function.returnType, packageName)
            is UnknownProjection -> replaceUnknownType(function.returnType, packageName)
            is ProjectionContainer -> {
                val types = returnType.projections
                        .plus(returnType.typeSuggestion)
                if (hasUnknownTypes(types)) {
                    replaceUnknownType(function.returnType, packageName)
                } else returnType
            }
            else -> returnType
        }
    }

    companion object {

        fun of(fileContents: Collection<KtFileContent>) = FunctionFiller(fileContents)

    }

}
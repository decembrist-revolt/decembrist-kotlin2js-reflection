package org.decembrist.fillers

import org.decembrist.domain.content.IContent
import org.decembrist.domain.content.KtFileContent
import org.decembrist.domain.content.functions.FunctionParameter
import org.decembrist.domain.content.functions.IFuncContent
import org.decembrist.domain.content.members.IMembered
import org.decembrist.services.TypeSuggestion
import java.util.Collections.singletonList

class FunctionFiller(val fileContents: Collection<KtFileContent>) {

    //TODO retrieve all classes by package pair
    private val functionItemList: List<FunctionItem>

    init {
        functionItemList = fileContents
                .map { fileContent ->
                    val packageName = fileContent.`package`?.name ?: ""
                    val content: MutableList<Pair<String, IContent>> = fileContent.classes
                            .map { packageName to it }
                            .toMutableList()
                    fileContent.functions
                            .map { packageName to it }
                            .let { content.addAll(it) }
                    return@map content
                }.flatten()
                .map { it.toFunctionItems() }
                .flatten()
    }

    /**
     * Fill missing annotation instance info from [fileContent]
     */
    fun fill(): Collection<KtFileContent> {
        for (fileContent in fileContents) {
            val methods = fileContent.classes
                    .filter { it is IMembered }
                    .map { (it as IMembered).methods }
                    .flatten()
            val unknownTypeFunctions = methods.filter { hasUnknownParameterTypes(it) }
        }
        return fileContents
    }

    private fun hasUnknownParameterTypes(function: IFuncContent): Boolean {
        val parameterTypes = function.functionParameters
                .map { it.type }
        return parameterTypes
                .plus(function.returnType)
                .any { it is TypeSuggestion.Unknown }
    }

    private fun Pair<String, IContent>.toFunctionItems(): List<FunctionItem> {
        val packageName = first
        val iContent = second
        return when (iContent) {
            is IFuncContent -> singletonList(FunctionItem(
                    iContent.functionParameters,
                    packageName,
                    iContent,
                    null
            ))
            is IMembered -> iContent.methods.map {
                FunctionItem(
                        it.functionParameters,
                        packageName,
                        it,
                        iContent
                )
            }
            else -> emptyList()
        }
    }

    private class FunctionItem(val functionParameters: List<FunctionParameter>,
                               val packageName: String,
                               val iFuncContent: IFuncContent,
                               val owner: IMembered?) {

//        override fun toString(): String {
//            return "@${concatenateClassName(annotationName, packageName)}" +
//                    "(${parameters.joinToString()})"
//        }

    }

}
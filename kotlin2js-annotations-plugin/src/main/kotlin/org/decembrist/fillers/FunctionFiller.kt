package org.decembrist.fillers

import org.decembrist.domain.content.IContent
import org.decembrist.domain.content.KtFileContent
import org.decembrist.domain.content.functions.FunctionParameter
import org.decembrist.domain.content.functions.IFuncContent
import org.decembrist.domain.content.members.IMembered
import java.util.Collections.singletonList

class FunctionFiller(val fileContents: Collection<KtFileContent>) {

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

        }
        return fileContents
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
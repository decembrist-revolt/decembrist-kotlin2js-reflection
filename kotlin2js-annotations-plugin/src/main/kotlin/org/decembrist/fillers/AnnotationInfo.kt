package org.decembrist.fillers

import com.squareup.kotlinpoet.TypeName
import org.decembrist.domain.content.KtFileContent
import org.decembrist.domain.content.classes.IClassContent

class AnnotationInfo(fileContents: Collection<KtFileContent>) {

//    private val annotationClasses: List<IClassContent>

    init {
//        annotationClasses = fileContents
//                .map { fileContent ->
//                    val packageName = fileContent.`package`?.name ?: ""
//                    fileContent.classes.map { packageName to it }
//                }.flatten()
//                .filter { it.second.isAnnotation }
//                .map {
//                    val packageName = it.first
//                    val annName = it.second.name
//                }
    }

    /**
     * Fill missing annotation instance info
     */
//    fun fill(fileContent: KtFileContent): KtFileContent {
//
//    }

    private class AnnotationInfo(val annName: String,
                                 val packageName: String,
                                 val attributes: List<AnnotationAttribute>)

    private class AnnotationAttribute(val name: String, val type: TypeName)

}
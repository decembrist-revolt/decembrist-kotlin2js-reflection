package org.decembrist.fillers

import org.decembrist.Message.concatenateClassName
import org.decembrist.Message.lineSeparator
import org.decembrist.domain.Attribute
import org.decembrist.domain.content.IAnnotated
import org.decembrist.domain.content.KtFileContent
import org.decembrist.domain.content.annotations.AnnotationClass
import org.decembrist.domain.content.annotations.AnnotationParameter
import org.decembrist.domain.content.classes.Class
import org.decembrist.services.AnnotationService
import org.decembrist.services.AnnotationService.hasUnknownAttributesInfo
import org.decembrist.services.AnnotationService.isAttributeWithUnknownName
import org.decembrist.services.AnnotationService.isAttributeWithUnknownType
import org.decembrist.services.TypeSuggestion
import org.decembrist.services.TypeSuggestion.Unknown
import org.decembrist.services.logging.LoggerService

class AnnotationInfoFiller(fileContents: Collection<KtFileContent>) {

    private val annotationItemList: List<AnnotationItem>
    private val embeddedJsItemList: List<AnnotationItem>

    init {
        embeddedJsItemList = AnnotationService.embededJsAnnotations
                .map { AnnotationItem(it.name, "", it.parameters) }
        annotationItemList = fileContents
                .map { fileContent ->
                    val packageName = fileContent.`package`?.name ?: ""
                    fileContent.classes.map { packageName to it }
                }.flatten()
                .filter { it.second is AnnotationClass }
                .map { it.first to it.second as AnnotationClass }
                .map {
                    val packageName = it.first
                    val annotation = it.second.name
                    val parameters = it.second.parameters
                    AnnotationItem(annotation, packageName, parameters)
                }
        LoggerService.debug("Supported annotations:")
        LoggerService.debug(annotationItemList.joinToString(lineSeparator))
    }

    /**
     * Fill missing annotation instance info from [fileContent]
     */
    fun fill(fileContent: KtFileContent) {
        val methods: List<IAnnotated> = fileContent.classes
                .filter { it is Class }
                .map { (it as Class).methods }
                .flatten()
        val annotatedEntities: List<IAnnotated> = fileContent.classes
                .plus(fileContent.functions)
                .plus(methods)
                .filter { it.annotations.isNotEmpty() }
        val packageName = fileContent.`package`?.name ?: ""
        for (entity in annotatedEntities) {
            fillItemAnnotation(entity, packageName)
        }
    }

    /**
     * Fill missing annotation instance info
     */
    private fun fillItemAnnotation(entity: IAnnotated,
                                   packageName: String) {
        val filledAnnotationsList = entity.annotations.map { annotation ->
            return@map try {
                val filledAttributes = if (hasUnknownAttributesInfo(annotation)) {
                    val annotationType = annotation.type
                    var annotationItem = getAnnotationItemByType(annotationType, packageName)
                    annotationItem = if (annotationItem == null && annotationType is Unknown) {
                        findInEmbeddings(annotationType)
                    } else annotationItem
                    if (annotationItem == null) {
                        throw AnnotationNotFoundException(annotationType.toString())
                    }
                    annotation.attributes.mapIndexed { index, attribute ->
                        val parameter = annotationItem.parameters[index]
                        return@mapIndexed fillAttribute(attribute, parameter)
                    }
                } else annotation.attributes
                annotation.apply {
                    attributes.clear()
                    attributes.addAll(filledAttributes)
                }
            } catch (ex: AnnotationNotFoundException) {
                LoggerService.warn(ex.message!!)
                null
            }
        }.filterNotNull()
        entity.apply {
            entity.annotations.clear()
            annotations.addAll(filledAnnotationsList)
        }
    }

    /**
     * Tries to find connected annotation class in class path
     */
    private fun getAnnotationItemByType(type: TypeSuggestion,
                                        packageName: String): AnnotationItem? {
        val annotationItem = annotationItemList.firstOrNull { annotationItem ->
            return@firstOrNull if (annotationItem.annotationName == type.type) {
                if (type is TypeSuggestion.Type) {
                    annotationItem.packageName == type.packageName
                } else {
                    annotationItem.packageName == packageName
                }
            } else false
        }
        return annotationItem
    }

    /**
     * Tries to find connected annotation class in kotlin js annotations
     */
    private fun findInEmbeddings(type: TypeSuggestion): AnnotationItem? {
        return embeddedJsItemList.firstOrNull { annotationItem ->
            annotationItem.annotationName == type.type
        }
    }

    private fun fillAttribute(attribute: Attribute,
                              parameter: AnnotationParameter): Attribute {
        return if (isAttributeWithUnknownName(attribute) or isAttributeWithUnknownType(attribute)) {
            Attribute(parameter.name, attribute.value, parameter.type)
        } else attribute
    }

    private class AnnotationItem(val annotationName: String,
                                 val packageName: String,
                                 val parameters: List<AnnotationParameter>) {

        override fun toString(): String {
            return "@${concatenateClassName(annotationName, packageName)}" +
                    "(${parameters.joinToString()})"
        }

    }

}
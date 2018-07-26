package org.decembrist.fillers

import org.decembrist.Message.lineSeparator
import org.decembrist.domain.Attribute
import org.decembrist.domain.content.IAnnotated
import org.decembrist.domain.content.KtFileContent
import org.decembrist.domain.content.annotations.AnnotationParameter
import org.decembrist.domain.content.classes.Class
import org.decembrist.domain.headers.annotations.AnnotationInstance
import org.decembrist.fillers.exceptions.AnnotationNotFoundException
import org.decembrist.services.AnnotationService.hasUnknownAttributesInfo
import org.decembrist.services.AnnotationService.isAttributeWithUnknownName
import org.decembrist.services.AnnotationService.isAttributeWithUnknownType
import org.decembrist.services.ReplaceTypeService.replaceUnknownType
import org.decembrist.services.cache.CacheService
import org.decembrist.services.logging.LoggerService
import org.decembrist.services.typesuggestions.TypeConstants
import org.decembrist.services.typesuggestions.TypeConstants.Companion.isArray
import org.decembrist.services.typesuggestions.TypeSuggestion
import org.decembrist.services.typesuggestions.TypeSuggestion.Unknown
import org.decembrist.services.typesuggestions.VarargsContainer

open class AnnotationInfoFiller private constructor(
        private val fileContents: Collection<KtFileContent>) {

    init {
        LoggerService.debug("Supported annotations:")
        LoggerService.debug(CacheService.getAnnotationCache().joinToString(lineSeparator))
    }

    /**
     * Fill missing annotation instance info from [fileContent]
     */
    fun fill(): Collection<KtFileContent> {
        for (fileContent in fileContents) {
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
        return fileContents
    }

    /**
     * Fill missing annotation instance info
     */
    private fun fillItemAnnotation(entity: IAnnotated,
                                   packageName: String) {
        val filledAnnotationsList = entity.annotations.map { annotation ->
            return@map try {
                var annotationItem: CacheService.AnnotationInstanceItem? = null
                val filledAttributes = if (hasUnknownAttributesInfo(annotation)) {
                    val annotationType = annotation.type
                    annotationItem = getAnnotationItemByType(annotationType, packageName)
                    annotationItem = if (annotationItem == null && annotationType is Unknown) {
                        findInEmbeddings(annotationType)
                    } else annotationItem
                    if (annotationItem == null) {
                        throw AnnotationNotFoundException(annotationType.toString())
                    }
                    fillAttributes(annotation.attributes, annotationItem, packageName)
                } else annotation.attributes
                val newAnnotation = if (annotationItem == null) {
                    annotation
                } else AnnotationInstance(annotationItem.getTypeSuggestion())
                newAnnotation.apply {
                    attributes = filledAttributes
                }
            } catch (ex: AnnotationNotFoundException) {
                LoggerService.warn(ex.message!!)
                null
            }
        }.filterNotNull().toSet()
        entity.apply {
            annotations = filledAnnotationsList
        }
    }

    private fun fillAttributes(attributes: List<Attribute>,
                               annotationItem: CacheService.AnnotationInstanceItem,
                               packageName: String): List<Attribute> {
        var varargParam: AnnotationParameter? = null
        val newAttributes: MutableList<Attribute> = mutableListOf()
        for (index in 0 until attributes.size) {
            val parameter = varargParam ?: annotationItem.parameters[index]
            val attribute = attributes[index]
            if (parameter.type is VarargsContainer) {
                if (varargParam == null) {
                    val newAttribute = fillAttribute(attribute, parameter, packageName)
                    newAttributes.add(newAttribute)
                    varargParam = parameter
                } else {
                    val newAttribute = newAttributes.pop()
                    val name = newAttribute.name
                    val type = newAttribute.type
                    val value = addVarargValue(newAttribute.value, attribute.value, type)
                    newAttributes.add(Attribute(name, value, type))
                }
            } else {
                val newAttribute = fillAttribute(attribute, parameter, packageName)
                newAttributes.add(newAttribute)
            }
        }
        return newAttributes
    }

    /**
     * Tries to find connected annotation class in class path
     */
    private fun getAnnotationItemByType(type: TypeSuggestion,
                                        packageName: String): CacheService.AnnotationInstanceItem? {
        return CacheService.getAnnotationCache().firstOrNull { annotationItem ->
            return@firstOrNull if (annotationItem.annotationName == type.type) {
                if (type is TypeSuggestion.Type) {
                    annotationItem.packageName == type.packageName
                } else {
                    annotationItem.packageName == packageName
                }
            } else false
        }
    }

    /**
     * Tries to find connected annotation class in kotlin js annotations
     */
    private fun findInEmbeddings(type: TypeSuggestion): CacheService.AnnotationInstanceItem? {
        return CacheService.getEmbeddedJsAnnotationCache().firstOrNull { annotationItem ->
            annotationItem.annotationName == type.type
        }
    }

    private fun fillAttribute(attribute: Attribute,
                              parameter: AnnotationParameter,
                              packageName: String): Attribute {
        return if (isAttributeWithUnknownName(attribute) or isAttributeWithUnknownType(attribute)) {
            val type = replaceUnknownType(parameter.type, packageName)
            val isVararg = parameter.type is VarargsContainer
            val value = fillValue(attribute.value, type, isVararg)
            Attribute(parameter.name, value, type)
        } else attribute
    }

    private fun addVarargValue(string: String, value: String, type: TypeSuggestion) = when {
        string.startsWith("arrayOf(") -> {
            val addition = string.replaceBeforeLast(")", ", $value")
            string.removeSuffix(")") + addition
        }
        else -> throw UnsupportedOperationException(
                "value $value not supported for annotation vararg parameter")
    }

    private tailrec fun fillValue(value: String,
                                  type: TypeSuggestion,
                                  isVararg: Boolean): String {
        val fixedValue = removeBraces(value)
        return when {
            isVararg -> when {
                fixedValue.startsWith("*") -> fillValue(
                        fixedValue.substringAfter("*"),
                        type,
                        false
                )
                else -> "arrayOf($fixedValue)"
            }
            isArray(type) -> when {
                fixedValue.startsWith("arrayOf(") -> fixedValue
                fixedValue.startsWith("[") && fixedValue.endsWith("]") -> {
                    val items = fixedValue.removeSurrounding("[", "]")
                    "arrayOf($items)"
                }
                else -> throw UnsupportedOperationException(
                        "value $fixedValue not supported for annotation vararg parameter")
            }
            else -> fixedValue
        }
    }

    private tailrec fun removeBraces(value: String): String {
        return if (value.startsWith("(")) {
            removeBraces(value.removeSurrounding("(", ")"))
        } else value
    }

    private fun MutableList<Attribute>.pop(): Attribute {
        val lastIndex = lastIndex
        val last = last()
        removeAt(lastIndex)
        return last
    }

    companion object {

        fun of(fileContents: Collection<KtFileContent>) = AnnotationInfoFiller(fileContents)

    }

}
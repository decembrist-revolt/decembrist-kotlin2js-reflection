package org.decembrist.services.cache

import org.decembrist.Message
import org.decembrist.domain.content.IVisibilityModified
import org.decembrist.domain.content.KtFileContent
import org.decembrist.domain.content.annotations.AnnotationClass
import org.decembrist.domain.content.annotations.AnnotationParameter
import org.decembrist.domain.content.classes.IEntityContent
import org.decembrist.services.AnnotationService
import org.decembrist.services.TypeService
import org.decembrist.services.typesuggestions.TypeSuggestion

object CacheService {

    private lateinit var classCache: List<CacheService.ClassItem>
    private lateinit var annotationCache: List<AnnotationInstanceItem>
    private lateinit var embeddedJsAnnotationCache: List<AnnotationInstanceItem>
    private val privateEntities: MutableList<ClassItem> = mutableListOf()
    private val privateAnnotations: MutableList<AnnotationInstanceItem> = mutableListOf()

    fun cache(ktFileContents: Collection<KtFileContent>) {
        cacheClasses(ktFileContents)
        cacheAnnotations(ktFileContents)
        cacheEmbeddedJsAnnotations()
    }

    fun cachePrivate(entity: IEntityContent, packageName: String) {
        if (entity is AnnotationClass) {
            val item =
                    CacheService.AnnotationInstanceItem(entity.name, packageName, emptyList())
            privateAnnotations.add(item)
        } else {
            val item = CacheService.ClassItem(entity.name, packageName)
            privateEntities.add(item)
        }
    }

    fun getClassCache() = classCache

    fun getAnnotationCache() = annotationCache

    fun getEmbeddedJsAnnotationCache() = embeddedJsAnnotationCache

    fun getPrivateAnnotations() = privateAnnotations.toList()

    private fun cacheClasses(ktFileContents: Collection<KtFileContent>) {
        classCache = ktFileContents.map { fileContent ->
            val packageName = fileContent.`package`?.name ?: ""
            return@map fileContent.classes.map { clazz ->
                CacheService.ClassItem(clazz.name, packageName)
            }
        }.flatten()
    }

    private fun cacheAnnotations(ktFileContents: Collection<KtFileContent>) {

        annotationCache = ktFileContents
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
                    CacheService.AnnotationInstanceItem(annotation, packageName, parameters)
                }
    }

    private fun cacheEmbeddedJsAnnotations() {
        embeddedJsAnnotationCache = AnnotationService.embededJsAnnotations
                .map { CacheService.AnnotationInstanceItem(it.name, "", it.parameters) }
    }

    class ClassItem(val className: String,
                    val packageName: String) {

        companion object {

            fun of(fullType: String): ClassItem {
                val (className, packageName) = TypeService.splitFullClassName(fullType)
                return ClassItem(className, packageName)
            }

        }

    }

    class AnnotationInstanceItem(val annotationName: String,
                                         val packageName: String,
                                         val parameters: List<AnnotationParameter>) {

        fun getTypeSuggestion() = TypeSuggestion.Type(
                annotationName,
                packageName
        )

        override fun toString(): String {
            return "@${Message.concatenateClassName(annotationName, packageName)}" +
                    "(${parameters.joinToString()})"
        }

    }

}
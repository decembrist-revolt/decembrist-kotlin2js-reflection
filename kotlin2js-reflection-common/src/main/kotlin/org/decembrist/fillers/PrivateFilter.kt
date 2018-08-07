package org.decembrist.fillers

import org.decembrist.Message.classIsPrivate
import org.decembrist.Message.entityIsPrivate
import org.decembrist.domain.content.IVisibilityModified
import org.decembrist.domain.content.KtFileContent
import org.decembrist.domain.content.classes.AbstractClass
import org.decembrist.domain.content.classes.IEntityContent
import org.decembrist.domain.content.functions.IFuncContent
import org.decembrist.domain.content.members.IMembered
import org.decembrist.services.cache.CacheService
import org.decembrist.services.logging.LoggerService
import kotlin.reflect.KVisibility

class PrivateFilter private constructor(
        private val fileContents: Collection<KtFileContent>) {

    fun filter(): Collection<KtFileContent> {
        for (fileContent in fileContents) {
            val packageName = fileContent.`package`?.name ?: ""
            filterClasses(fileContent, packageName)
            filterMethods(fileContent, packageName)
            filterFunctions(fileContent, packageName)
        }
        return fileContents
    }

    private fun filterFunctions(fileContent: KtFileContent, packageName: String) {
        val functions = mutableSetOf<IFuncContent>()
        for (function in fileContent.functions) {
            if (!isPrivateEntity(function, packageName)) {
                functions.add(function)
            }
        }
        fileContent.functions = functions
    }

    private fun filterMethods(fileContent: KtFileContent, packageName: String) {
        for (clazz in fileContent.classes) {
            if (clazz is IMembered) {
                val iterator = clazz.methods.iterator()
                while (iterator.hasNext()) {
                    val method = iterator.next()
                    if (isPrivateEntity(method, packageName)) {
                        iterator.remove()
                    }
                }
            }
        }
    }

    private fun filterClasses(fileContent: KtFileContent, packageName: String) {
        val classes = mutableListOf<AbstractClass>()
        for (clazz in fileContent.classes) {
            if (!isPrivateEntity(clazz, packageName)) {
                classes.add(clazz)
            }
        }
        fileContent.classes = classes
    }

    private fun isPrivateEntity(entity: IEntityContent, packageName: String): Boolean {
        return if (isPrivate(entity)) {
            LoggerService.warn(entityIsPrivate(entity.name))
            CacheService.cachePrivate(entity, packageName)
            true
        } else false
    }

    private fun isPrivate(visibilityModified: IVisibilityModified) =
            visibilityModified.visibility == KVisibility.PRIVATE

    companion object {

        fun of(fileContents: Collection<KtFileContent>) = PrivateFilter(fileContents)

    }
}
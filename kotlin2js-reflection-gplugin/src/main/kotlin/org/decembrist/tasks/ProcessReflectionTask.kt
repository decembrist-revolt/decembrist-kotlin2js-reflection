package org.decembrist.tasks

import org.decembrist.Kotlin2jsReflectionPluginExtension
import org.decembrist.exceptions.Kotlin2JsPluginNotFoundException
import org.decembrist.generators.ReflectionUtilsGenerator
import org.decembrist.parsers.SourceParser
import org.decembrist.services.logging.LoggerService
import org.decembrist.utils.getReflectionExtension
import org.decembrist.writers.WriteFile
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.plugin.Kotlin2JsPluginWrapper
import java.io.File
import java.io.FileNotFoundException

open class ProcessReflectionTask : DefaultTask() {

    lateinit var extension: Kotlin2jsReflectionPluginExtension

    @TaskAction
    fun run() {
        extension = project.extensions.getReflectionExtension()
        checkKotlin2JsPluginExistence()
        val sourceDirectories = getSourceDirectories()
        val sourceParser = SourceParser(sourceDirectories)
        val contentList = sourceParser.parse()
        val generator = ReflectionUtilsGenerator()
        val fileSpecs = generator.generateCode(contentList)
        val generatedSourcesDir = extension.generatedSourcesDir
        val writeFile = WriteFile(generatedSourcesDir)
        fileSpecs.forEach(writeFile::write)
    }

    private fun getSourceDirectories(): List<File> {
        return extension.sourceDirectories.map { sourceDirectory ->
            if (sourceDirectory.exists().and(sourceDirectory.isDirectory)) {
                sourceDirectory
            } else throw FileNotFoundException(
                    "one of sourceDirectories param = $sourceDirectory dir not found")
        }
    }

    private fun checkKotlin2JsPluginExistence() {
        val kotlin2JsPlugin: Kotlin2JsPluginWrapper? = project.plugins
                .firstOrNull { it is Kotlin2JsPluginWrapper }
                ?.let { it as Kotlin2JsPluginWrapper }
        val kotlin2JsPluginNotFoundException = Kotlin2JsPluginNotFoundException()
        kotlin2JsPlugin ?: if (extension.checkKotlin2JsPluginExistence) {
            throw kotlin2JsPluginNotFoundException
        } else LoggerService.warn(kotlin2JsPluginNotFoundException.message!!)
        kotlin2JsPlugin?.let { kotlin2JsPluginWrapper ->
            compareKotlin2JsVersions(kotlin2JsPluginWrapper.kotlinPluginVersion)
        }
    }


    private fun compareKotlin2JsVersions(version: String) {
        if (KOTLIN2JS_PLUGIN_VERSION != version) {
            LoggerService.warn("""$version does not equal recommended Kotlin2JsPlugin version
                |recommended version is $KOTLIN2JS_PLUGIN_VERSION""".trimMargin())
        }
    }

    companion object {

        const val KOTLIN2JS_PLUGIN_VERSION = "1.2.51"

    }

}
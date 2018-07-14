package org.decembrist

import org.decembrist.exceptions.TaskNotFoundException
import org.decembrist.services.logging.Logger
import org.decembrist.services.logging.LoggerService
import org.decembrist.tasks.ProcessReflectionTask
import org.decembrist.utils.getReflectionExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File
import java.util.Collections.singletonList
import org.gradle.api.logging.Logger as GradleLogger


class Kotlin2jsReflectionPlugin : Plugin<Project> {

    private lateinit var processReflectionTask: ProcessReflectionTask

    override fun apply(project: Project) {
        LoggerService.logger = EnvLogger(project.logger)
        setUpExtension(project)
        project.afterEvaluate(::setUpReflectionTask)
        processReflectionTask = project.tasks
                .create("generateReflectionInfo", ProcessReflectionTask::class.java)
    }

    /**
     * Set up plugin extension defaults
     */
    private fun setUpExtension(project: Project) {
        val kotlinFolder = getKotlinFolder(project)
        val extension = project.extensions
                .create(
                        "kotlin2JsReflection",
                        Kotlin2jsReflectionPluginExtension::class.java)
        extension.checkKotlin2JsPluginExistence = true
        extension.executeBefore = BEFORE_TASK
        extension.sourceDirectories = singletonList(kotlinFolder)
        extension.generatedSourcesDir = getGeneratedSourceDirectory(project)
    }

    private fun getGeneratedSourceDirectory(project: Project): File {
        val separator = File.separator
        val projectBuildDir = project.project.buildDir
        return File(projectBuildDir, "generated${separator}source${separator}decembrist")
    }

    private fun executeReflectionProcessing(task: Task) {
        processReflectionTask.run()
    }

    private fun setUpReflectionTask(project: Project) {
        val extension = project.extensions.getReflectionExtension()
        val task = project.tasks.findByName(extension.executeBefore)
        task ?: throw TaskNotFoundException(extension.executeBefore)
        task.doFirst(::executeReflectionProcessing)
    }

    private fun getKotlinFolder(project: Project): File {
        val projectDir = project.projectDir.absolutePath
        val separator = File.separator
        return File(projectDir, "src${separator}main${separator}kotlin")
    }

    internal class EnvLogger(private val logger: GradleLogger) : Logger {

        override fun debug(message: String) {
            logger.debug(message)
        }

        override fun warn(message: String) {
            logger.warn(message)
        }

    }

    companion object {

        const val BEFORE_TASK = "compileKotlin2Js"

    }

}
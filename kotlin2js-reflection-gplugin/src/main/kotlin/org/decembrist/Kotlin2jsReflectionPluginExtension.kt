package org.decembrist

import java.io.File

open class Kotlin2jsReflectionPluginExtension() {

    /**
     * Check kotlin plugin before run
     */
    var checkKotlin2JsPluginExistence: Boolean = true

    /**
     * Task name that will be used to run plugin before
     */
    lateinit var executeBefore: String

    /**
     * Source dir for annotation processing
     */
    lateinit var sourceDirectories: List<File>

    /**
     * Directory for plugin generated sources
     */
    lateinit var generatedSourcesDir: File

    constructor(checkKotlin2JsPluginExistence: Boolean,
                executeBefore: String,
                sourceDirectories: List<File>,
                generatedSourcesDir: File) : this() {
        this.checkKotlin2JsPluginExistence = checkKotlin2JsPluginExistence
        this.executeBefore = executeBefore
        this.sourceDirectories = sourceDirectories
        this.generatedSourcesDir = generatedSourcesDir
    }

}
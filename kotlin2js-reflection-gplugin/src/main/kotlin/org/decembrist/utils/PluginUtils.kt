package org.decembrist.utils

import org.decembrist.Kotlin2jsReflectionPluginExtension
import org.gradle.api.plugins.ExtensionContainer

fun ExtensionContainer.getReflectionExtension() = findByName("kotlin2JsReflection")
        .let { it as Kotlin2jsReflectionPluginExtension }
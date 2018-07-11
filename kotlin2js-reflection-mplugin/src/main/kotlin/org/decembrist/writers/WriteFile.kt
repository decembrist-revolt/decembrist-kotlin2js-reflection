package org.decembrist.writers

import com.squareup.kotlinpoet.FileSpec
import java.io.File

class WriteFile(private val genSourcesDir: File) {

    init {
        if (genSourcesDir.exists()) genSourcesDir.deleteRecursively()
        genSourcesDir.mkdirs()
    }

    fun write(fileSpec: FileSpec) {
        fileSpec.writeTo(genSourcesDir)
    }

}
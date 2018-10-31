package org.decembrist.services

import java.io.File

object FilesService {

    /**
     * Flat all files from [file] to one list
     */
    fun flatFiles(file: File): List<File> = if (file.isDirectory) {
        file.listFiles()
                .map { flatFiles(it) }
                .flatten()
    } else listOf(file)

}
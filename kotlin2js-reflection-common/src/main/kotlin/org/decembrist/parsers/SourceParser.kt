package org.decembrist.parsers

import org.decembrist.Message.folderExistenceFailedMessage
import org.decembrist.domain.content.KtFileContent
import org.decembrist.fillers.AnnotationInfoFiller
import org.decembrist.fillers.FunctionFiller
import org.decembrist.fillers.PrivateFilter
import org.decembrist.services.FilesService.flatFiles
import org.decembrist.services.cache.CacheService
import java.io.File

class SourceParser(private val sourceDirs: Collection<File>) {

    init {
        checkSources()
    }

    /**
     * Parse and fill kt files
     */
    fun parse(): Collection<KtFileContent> {
        val ktFiles = sourceDirs
                .map { flatFiles(it) }
                .flatten()
                .filter { it.extension == "kt" }
        return ktFiles
                .map { KtFileParser.of(it).parse() }
                .let { PrivateFilter.of(it).filter() }
                .apply { CacheService.cache(this) }
                .let { AnnotationInfoFiller.of(it).fill() }
                .let { FunctionFiller.of(it).fill() }
    }

    /**
     * Every folder should exist or be folder
     */
    private fun checkSources() = sourceDirs.forEach(::checkFolderExistence)

    /**
     * Throw exception if any source is not folder or doesn't exist
     */
    private fun checkFolderExistence(folder: File) {
        if ((folder.exists() and folder.isDirectory).not())
            throw IllegalArgumentException(folderExistenceFailedMessage(folder.path))
    }
}
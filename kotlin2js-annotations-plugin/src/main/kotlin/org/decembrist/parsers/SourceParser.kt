package org.decembrist.parsers

import org.decembrist.Message.folderExistenceFailedMessage
import org.decembrist.domain.content.KtFileContent
import org.decembrist.fillers.AnnotationInfoFiller
import org.decembrist.services.FilesService.flatFiles
import java.io.File

class SourceParser(private val sourceDirs: Collection<File>) {

    init {
        checkSources()
    }

    fun parse(): List<KtFileContent> {
        val ktFiles = sourceDirs
                .map { flatFiles(it) }
                .flatten()
                .filter { it.extension == "kt" }
        val fileContentList = ktFiles.map { KtFileParser(it).parse() }
        val annotationInfoFiller = AnnotationInfoFiller(fileContentList)
        for (ktFileContent in fileContentList) {
            annotationInfoFiller.fill(ktFileContent)
        }
        return fileContentList
    }

    private fun checkSources() = sourceDirs.forEach(::checkFolderExistance)

    private fun checkFolderExistance(folder: File) {
        if (!(folder.exists() and folder.isDirectory))
            throw IllegalArgumentException(folderExistenceFailedMessage(folder.path))
    }
}
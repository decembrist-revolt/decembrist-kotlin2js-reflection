package org.decembrist.parsers

import org.decembrist.parser.KotlinLexer
import org.decembrist.parser.KotlinParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.decembrist.domain.content.KtFileContent
import java.io.File

open class KtFileParser private constructor(private val ktFile: File) : IParser<KtFileContent> {

    override fun parse(): KtFileContent {
        val fileListener = KtFileListener(ktFile.name)
        val tree = ktFile.inputStream()
                .let { CharStreams.fromStream(it) }
                .let { KotlinLexer(it) }
                .let { CommonTokenStream(it) }
                .let { KotlinParser(it) }
                .kotlinFile()
        ParseTreeWalker().walk(fileListener, tree)
        return fileListener.fileContent
    }

    companion object {

        fun of(ktFile: File) = KtFileParser(ktFile)

    }

}
package org.decembrist.parsers

import com.github.sarahbuisson.kotlinparser.KotlinLexer
import com.github.sarahbuisson.kotlinparser.KotlinParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.decembrist.domain.content.KtFileContent
import java.io.File

class KtFileParser(val ktFile: File) : IParser<KtFileContent> {

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

}
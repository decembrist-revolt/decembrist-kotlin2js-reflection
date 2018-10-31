package org.decembrist.generators

import com.squareup.kotlinpoet.CodeBlock
import org.decembrist.domain.content.IContent

interface IGenerator<T: IContent> {

    fun generate(content: T): CodeBlock

}
package org.decembrist.generators

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import org.decembrist.Message.lineSeparator

fun CodeBlock.Builder.nextLine() = add(lineSeparator)

fun CodeBlock.Builder.addImport(import: String) =
        add("%T", ClassName("$import//", ""))
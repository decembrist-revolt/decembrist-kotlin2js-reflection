package org.decembrist.generators

import com.squareup.kotlinpoet.CodeBlock
import org.decembrist.Message.lineSeparator

fun CodeBlock.Builder.nextLine() = add(lineSeparator)
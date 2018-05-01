package org.decembrist.domain.content

import org.decembrist.domain.content.classes.IClassContent
import org.decembrist.domain.content.functions.IFuncContent

class KtFileContent(val name: String) : IContent {

    var `package`: Package? = null

    var imports: ImportsContent? = null

    var classes: Set<IClassContent> = mutableSetOf()

    var functions: Set<IFuncContent> = mutableSetOf()

}
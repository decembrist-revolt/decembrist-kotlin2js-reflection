package org.decembrist.domain.content

import org.decembrist.domain.content.classes.AbstractClass
import org.decembrist.domain.content.functions.IFuncContent

class KtFileContent(val name: String) : IContent {

    var `package`: Package? = null

    var imports: ImportsContent? = null

    var classes: List<AbstractClass> = emptyList()

    var functions: Set<IFuncContent> = emptySet()

}
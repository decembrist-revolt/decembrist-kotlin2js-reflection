package org.decembrist.parsers

import org.decembrist.domain.content.IContent

interface IParser<T: IContent> {

    fun parse(): T

}

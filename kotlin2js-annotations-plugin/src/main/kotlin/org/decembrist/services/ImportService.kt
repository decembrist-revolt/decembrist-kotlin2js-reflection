package org.decembrist.services

import org.decembrist.domain.Import

object ImportService {

    fun findFullClass(imports: Collection<Import>, className: String): String {
        val import = imports
                .map(Import::className)
                .firstOrNull { it.substringAfterLast(".") == className }
        return import ?: className
    }

}
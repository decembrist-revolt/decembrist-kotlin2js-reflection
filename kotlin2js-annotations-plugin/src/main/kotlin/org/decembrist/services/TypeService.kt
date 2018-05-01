package org.decembrist.services

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

object TypeService {

    fun getTypeName(className: String): TypeName {
        val packageName = className.substringBeforeLast(".")
        val clazz = className.substringAfterLast(".")
        return if (packageName == className) {
            ClassName("", clazz)
        } else {
            ClassName(packageName, clazz)
        }
    }

}
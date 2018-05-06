package org.decembrist.services

import org.decembrist.services.TypeSuggestion.Type
import org.decembrist.services.TypeSuggestion.Unknown

object TypeService {

    /**
     * @return connected [Type] or [Unknown] on [className] without package
     */
    fun getTypeSuggestion(className: String): TypeSuggestion {
        val packageName = className.substringBeforeLast(".")
        val clazz = className.substringAfterLast(".")
        return if (packageName == className) {
            Unknown(clazz)
        } else {
            Type(clazz, packageName)
        }
    }

}
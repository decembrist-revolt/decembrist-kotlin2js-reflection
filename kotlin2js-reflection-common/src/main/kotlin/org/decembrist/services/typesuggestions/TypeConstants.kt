package org.decembrist.services.typesuggestions

import org.decembrist.services.typesuggestions.TypeSuggestion.Type

enum class TypeConstants(val type: TypeSuggestion) {

    ARRAY(Type("Array", "kotlin")),
    INT_ARRAY(Type("IntArray", "kotlin")),
    FLOAT_ARRAY(Type("FloatArray", "kotlin")),
    LONG_ARRAY(Type("LongArray", "kotlin")),
    SHORT_ARRAY(Type("ShortArray", "kotlin")),
    BOOLEAN_ARRAY(Type("BooleanArray", "kotlin")),
    BYTE_ARRAY(Type("ByteArray", "kotlin")),
    CHAR_ARRAY(Type("CharArray", "kotlin")),
    DOUBLE_ARRAY(Type("DoubleArray", "kotlin"));

    companion object {

        fun getTypedArrayByType(className: String) = when (className) {
            "Int" -> INT_ARRAY
            "Float" -> FLOAT_ARRAY
            "Long" -> LONG_ARRAY
            "Short" -> SHORT_ARRAY
            "Boolean" -> BOOLEAN_ARRAY
            "Byte" -> BYTE_ARRAY
            "Char" -> CHAR_ARRAY
            "Double" -> DOUBLE_ARRAY
            else -> null
        }

        fun isStringArray(type: TypeSuggestion): Boolean {
            return if (type is TypeSuggestion.ProjectionContainer) {
                if (type.typeSuggestion == ARRAY || type.typeSuggestion == Type("Array", "")) {
                    val projection = type.projections.first()
                    projection == Type("String", "")
                            || projection == Type("String", "kotlin")
                } else false
            } else false
        }

        fun isArray(type: TypeSuggestion): Boolean {
            return if (type is TypeSuggestion.ProjectionContainer) {
                equalsIgnorePackage(type.typeSuggestion, ARRAY)
            } else when {
                equalsIgnorePackage(type, INT_ARRAY) -> true
                equalsIgnorePackage(type, FLOAT_ARRAY) -> true
                equalsIgnorePackage(type, LONG_ARRAY) -> true
                equalsIgnorePackage(type, SHORT_ARRAY) -> true
                equalsIgnorePackage(type, BOOLEAN_ARRAY) -> true
                equalsIgnorePackage(type, BYTE_ARRAY) -> true
                equalsIgnorePackage(type, CHAR_ARRAY) -> true
                equalsIgnorePackage(type, DOUBLE_ARRAY) -> true
                else -> false
            }
        }

        fun equalsIgnorePackage(type: TypeSuggestion, constant: TypeConstants): Boolean {
            return if (type == constant.type) true else {
                if (type is Type) {
                    if (type.packageName == "") {
                        type.type == constant.type.type
                    } else false
                } else false
            }
        }

    }

}
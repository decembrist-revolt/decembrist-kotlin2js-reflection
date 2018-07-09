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

    }

}
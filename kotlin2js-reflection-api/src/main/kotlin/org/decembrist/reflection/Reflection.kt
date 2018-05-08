package org.decembrist.reflection

class Reflection private constructor(val annotations: dynamic) {

    fun getAnnotations(item: Any): List<Annotation> {
        return annotations[item.toString()] ?: emptyList()
    }

    companion object {

        private val reflection: Reflection? = null

        fun of(annotations: dynamic) = reflection ?: Reflection(annotations)

    }
}
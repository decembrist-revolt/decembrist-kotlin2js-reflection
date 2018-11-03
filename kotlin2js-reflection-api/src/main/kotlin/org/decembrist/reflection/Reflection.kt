package org.decembrist.reflection

import org.decembrist.model.ClassInfo
import org.decembrist.model.FunctionIdentifier
import org.decembrist.model.MethodInfo
import org.decembrist.utils.getIdentifier
import org.decembrist.utils.isMethod
import org.decembrist.utils.putAndCheck
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

object Reflection {

    private var init = false

    private val FUNCTION_ANNOTATIONS: HashMap<FunctionIdentifier, List<Annotation>> = HashMap()
    private val classes: MutableMap<KClass<*>, ClassInfo<*>> = mutableMapOf()

    /**
     * Get annotations for class
     * @param kClass
     * @return annotations list
     */
    fun getAnnotations(kClass: KClass<*>): List<Annotation> {
        initialized()
        return classes[kClass]?.annotations ?: emptyList()
    }

    /**
     * Get annotations for function or class method (if present)
     * @param kClass class
     * @param kFunction function or method
     * @return annotations list
     */
    fun getAnnotations(kFunction: KFunction<*>, kClass: KClass<*>? = null): List<Annotation> {
        if (initialized().not()) {
            console.warn("Reflection data has not been set")
            return emptyList()
        }
        val identifier = getIdentifier(kFunction)
        return if (kClass == null) {
            if (identifier.isMethod()) {
                throw RuntimeException(
                        "${identifier.name} is method, thus kClass should be presented")
            } else null
        } else {
            if (identifier.isMethod()){
                classes[kClass]
                        ?.methods
                        ?.firstOrNull { it.method == identifier }
                        ?.annotations
            } else null
        } ?: FUNCTION_ANNOTATIONS[identifier] ?: emptyList()
    }

    /**
     * Get methods for class
     * @param kClass
     * @return methods list
     */
    fun getMethods(kClass: KClass<*>): List<MethodInfo> {
        return classes[kClass]
                ?.methods
                .orEmpty()
    }

    @Deprecated("Don't use it")
    fun setData(functionAnnotations: Map<FunctionIdentifier, List<Annotation>>,
                classes: List<ClassInfo<*>>) {
        init = true
        this.FUNCTION_ANNOTATIONS.putAll(functionAnnotations)
        val classesMap = classes
                .map { it.clazz to it }
                .toMap()
        this.classes.putAndCheck(classesMap)
    }

    private fun initialized() = init

}
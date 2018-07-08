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

    fun getAnnotations(kClass: KClass<*>): List<Annotation> {
        checkInit()
        return classes[kClass]?.annotations ?: emptyList()
    }

    fun getAnnotations(kFunction: KFunction<*>, kClass: KClass<*>? = null): List<Annotation> {
        checkInit()
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

    fun setData(functionAnnotations: Map<FunctionIdentifier, List<Annotation>>,
                classes: List<ClassInfo<*>>) {
        init = true
        this.FUNCTION_ANNOTATIONS.putAll(functionAnnotations)
        val classesMap = classes
                .map { it.clazz to it }
                .toMap()
        this.classes.putAndCheck(classesMap)
    }

    fun getMethods(kClass: KClass<*>): List<MethodInfo> {
        return classes[kClass]
                ?.methods
                .orEmpty()
    }

    private fun checkInit() {
        if (init.not()) throw RuntimeException("Reflection data has not been set")
    }

}
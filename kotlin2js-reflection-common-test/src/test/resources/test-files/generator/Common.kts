import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * Code for evaluating js reflection
 */

fun <T : KFunction<*>> getIdentifierBySupplier(kFunction: T): FunctionIdentifier {
    return FunctionIdentifier(
            kFunction.name,
            kFunction.toString()
    )
}

data class FunctionIdentifier(val name: String, val body: String) {

    override fun toString(): String {
        return """
            {
                "name": "$name",
                "body": "$body"
            }
        """.trimIndent()
    }
}

class ClassInfo<T : Any>(val clazz: KClass<T>,
                         val methods: List<MethodInfo>,
                         val annotations: List<Annotation>)

class MethodInfo(val method: FunctionIdentifier,
                 val annotations: List<Annotation>)

class JsReflect<T : Any>(val kClass: KClass<T>) {
    fun createInstance(args: Array<Any>): T {
        return kClass.primaryConstructor!!.call(*args)
    }
}

fun MutableMap<FunctionIdentifier, List<Annotation>>.putAndCheck(
        functionIdentifier: FunctionIdentifier,
        annotations: List<Annotation>) {
    this.put(functionIdentifier, annotations)
}

val KClass<out Annotation>.jsReflect
    get() = JsReflect(this)

object Reflection {

    lateinit var functionAnnotations: Map<FunctionIdentifier, List<Annotation>>
    lateinit var classes: List<ClassInfo<*>>

    fun setData(functionAnnotations: Map<FunctionIdentifier, List<Annotation>>,
                classes: List<ClassInfo<*>>) {
        this.functionAnnotations = functionAnnotations
        this.classes = classes
    }

    override fun toString(): String {
        val functions = functionAnnotations.map { entry ->
            val functionIdentifier = entry.key
            val annotations = entry.value
                    .joinToString(",", "[", "]", transform = ::parseAnnotation)
            return@map """{
                |   "function": $functionIdentifier,
                |   "annotations": $annotations
                |}""".trimMargin()
        }.joinToString(",", "[", "]")
        return """
            {
                "functions": $functions
            }
        """.trimIndent()
    }

    private fun <T : Annotation> parseAnnotation(annotation: T): String {
        val name = annotation.toString()
                .substringBefore("(")
                .removePrefix("@")
        val params = annotation.annotationClass.memberProperties
                .joinToString(",", "[", "]") { param ->
                    val value = annotation.annotationClass.java
                            .methods
                            .first { it.name == param.name }
                            .invoke(annotation)
                    """{
                "name": "${param.name}",
                "type": "${param.returnType}",
                "value": "$value"
            }""".trimIndent()
                }
        return """
        {
            "name": "$name",
            "params": $params
        }
    """.trimIndent()
    }

}
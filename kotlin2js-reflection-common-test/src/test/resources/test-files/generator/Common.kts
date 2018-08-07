import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters

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
        val primaryConstructor = kClass.primaryConstructor
        return if (primaryConstructor != null) {
            val returnTypes = primaryConstructor.parameters
                    .map { it.type }
            val castedArgs = args.mapIndexed { index, arg ->
                val type = returnTypes[index]
                val clazz = type.classifier as KClass<*>
                return@mapIndexed castByType(clazz, arg)
            }.toTypedArray()
            primaryConstructor.call(*castedArgs)
        } else {
            kClass.createInstance()
        }
    }
    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> castByType(clazz: KClass<T>, arg: Any): T {
        return when (clazz) {
            String::class -> arg as T
            Byte::class -> (arg as Int).toByte() as T
            Short::class -> (arg as Int).toShort() as T
            ByteArray::class -> (arg as Array<Int>)
                    .map { castByType(Byte::class, it) }
                    .toByteArray() as T
            ShortArray::class -> (arg as Array<Int>)
                    .map { castByType(Short::class, it) }
                    .toShortArray() as T
            IntArray::class -> (arg as Array<Int>).toIntArray() as T
            LongArray::class -> (arg as Array<Long>).toLongArray() as T
            FloatArray::class -> (arg as Array<Float>).toFloatArray() as T
            DoubleArray::class -> (arg as Array<Double>).toDoubleArray() as T
            CharArray::class -> (arg as Array<Char>).toCharArray() as T
            BooleanArray::class -> (arg as Array<Boolean>).toBooleanArray() as T
            else -> arg as T
        }
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
        val classes = this.classes.map { clazz ->
            val methods = clazz.methods.joinToString(",", "[", "]") { method ->
                val annotations = method.annotations
                        .joinToString(",", "[", "]", transform = ::parseAnnotation)
                return@joinToString """{
                    |   "function": ${method.method},
                    |   "annotations": $annotations
                    |}""".trimMargin()
            }
            val annotations = clazz.annotations
                    .joinToString(",", "[", "]", transform = ::parseAnnotation)
            return@map """{
                |   "name": "${clazz.clazz.qualifiedName}",
                |   "methods": $methods,
                |   "annotations": $annotations
                |}""".trimMargin()
        }.joinToString(",", "[", "]")
        return """
            {
                "functions": $functions,
                "classes": $classes
            }
        """.trimIndent()
    }

    private fun <T : Annotation> parseAnnotation(annotation: T): String {
        val name = annotation.toString()
                .substringBefore("(")
                .removePrefix("@")
        val primaryConstructor = annotation.annotationClass.primaryConstructor
        val params = primaryConstructor?.parameters
                ?.joinToString(",", "[", "]") { param ->
                    val value = annotation.annotationClass.java
                            .methods
                            .first { it.name == param.name }
                            .invoke(annotation)
                            .let(::valueToString)
                    return@joinToString """{
                "name": "${param.name}",
                "type": "${param.type}",
                "value": $value
            }""".trimIndent()
                } ?: "[]"
        return """
        {
            "name": "$name",
            "params": $params
        }
    """.trimIndent()
    }

    private fun valueToString(value: Any): String {
        return if (value is Array<*>) {
            value.joinToString(",", "[", "]") {
                """"$it""""
            }
        } else when (value) {
            is ByteArray -> value.joinToString(",", "[", "]") {
                """"$it""""
            }
            is ShortArray -> value.joinToString(",", "[", "]") {
                """"$it""""
            }
            is IntArray -> value.joinToString(",", "[", "]") {
                """"$it""""
            }
            is LongArray -> value.joinToString(",", "[", "]") {
                """"$it""""
            }
            is FloatArray -> value.joinToString(",", "[", "]") {
                """"$it""""
            }
            is DoubleArray -> value.joinToString(",", "[", "]") {
                """"$it""""
            }
            is CharArray -> value.joinToString(",", "[", "]") {
                """"$it""""
            }
            is BooleanArray -> value.joinToString(",", "[", "]") {
                """"$it""""
            }
            else -> """["$value"]"""
        }
    }

}
annotation class TestAnnotation1(val string: String,
                                val byte: Byte,
                                val short: Short,
                                val int: Int,
                                val long: Long,
                                val float: Float,
                                val double: Double,
                                val char: Char,
                                val bool: Boolean)

@TestAnnotation1("test", 1, 1, 1, 1L, 1.0f, 1.0, 't', true)
fun main(args: Array<String>) {

}
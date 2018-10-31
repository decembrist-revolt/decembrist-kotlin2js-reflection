annotation class TestAnnotation2(val string: Array<String>,
                                 val byte: ByteArray,
                                 val short: ShortArray,
                                 val int: IntArray,
                                 val long: LongArray,
                                 val float: FloatArray,
                                 val double: DoubleArray,
                                 val char: CharArray,
                                 val bool: BooleanArray)

@TestAnnotation2(["test1", "test2"], [1, 2], [1, 2], [1, 2], [1L, 2L], [1.0f, 2.0f], [1.0, 2.0], ['1', '2'], [true, false])
fun main(args: Array<String>) {

}
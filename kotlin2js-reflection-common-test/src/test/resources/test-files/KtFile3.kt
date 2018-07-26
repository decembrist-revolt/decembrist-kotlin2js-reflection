annotation class StringVarargAnnotation(vararg val string: String)
annotation class ByteVarargAnnotation(vararg val byte: Byte)
annotation class ShortVarargAnnotation(vararg val short: Short)
annotation class IntVarargAnnotation(vararg val int: Int)
annotation class LongVarargAnnotation(vararg val long: Long)
annotation class FloatVarargAnnotation(vararg val float: Float)
annotation class DoubleVarargAnnotation(vararg val double: Double)
annotation class CharVarargAnnotation(vararg val char: Char)
annotation class BooleanVarargAnnotation(vararg val bool: Boolean)

@StringVarargAnnotation("test1", "test2")
@ByteVarargAnnotation(1, 2)
@ShortVarargAnnotation(1, 2)
@IntVarargAnnotation(1, 2)
@LongVarargAnnotation(1L, 2L)
@FloatVarargAnnotation(1.0f, 2.0f)
@DoubleVarargAnnotation(1.0, 2.0)
@CharVarargAnnotation('1', '2')
@BooleanVarargAnnotation(true, false)
fun main(args: Array<String>) {

}
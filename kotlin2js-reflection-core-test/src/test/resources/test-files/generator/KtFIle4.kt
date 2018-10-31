annotation class Annotation4(vararg val string: String)
annotation class Annotation5(vararg val byte: Byte)
annotation class Annotation6(vararg val short: Short)
annotation class Annotation7(vararg val int: Int)
annotation class Annotation8(vararg val long: Long)
annotation class Annotation9(vararg val float: Float)
annotation class Annotation10(vararg val double: Double)
annotation class Annotation11(vararg val char: Char)
annotation class Annotation12(vararg val bool: Boolean)

@Annotation4(*arrayOf("test1", "test2"))
@Annotation5(1, 2)
@Annotation6(1, 2)
@Annotation7(*[1, 2])
@Annotation8(*[1L, 2L])
@Annotation9(*[1.0f, 2.0f])
@Annotation10(*[1.0, 2.0])
@Annotation11(*['1', '2'])
@Annotation12(*[true, false])
fun main(args: Array<String>) {

}
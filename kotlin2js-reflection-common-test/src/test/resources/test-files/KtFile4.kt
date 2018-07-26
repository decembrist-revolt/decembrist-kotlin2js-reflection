annotation class SpreadVarargAnnotation(vararg val string: String)
annotation class SquaredAnnotation(val string: Array<String>)
annotation class BracedAnnotation(val string: Array<String>)
annotation class SquaredVarargsAnnotation(vararg val string: String)


@SpreadVarargAnnotation(*arrayOf("test1", "test2"))
@SquaredAnnotation(["test1", "test2"])
@BracedAnnotation((["test1", "test2"]))
@SquaredVarargsAnnotation(*["test1", "test2"])
fun main(args: Array<String>) {

}
annotation class ClassAnnotation(val string: String)
annotation class MethodAnnotation(val string: String)

@ClassAnnotation("test1")
class B {

    @MethodAnnotation("test2")
    fun method(){

    }

}
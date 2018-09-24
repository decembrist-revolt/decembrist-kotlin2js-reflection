import org.decembrist.ClassAnnotation
import org.decembrist.MethodAnnotation

annotation class UnpackagedAnnotation(val name: String)

@ClassAnnotation
@UnpackagedAnnotation("unpackaged")
class UnpackagedClass {

    fun someMethod() = "test1"

    @MethodAnnotation
    fun someMethod(param: String): String {
        return param
    }

}
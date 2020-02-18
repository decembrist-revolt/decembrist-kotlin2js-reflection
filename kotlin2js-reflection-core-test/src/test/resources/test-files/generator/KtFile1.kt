annotation class Annotation1(val name: String)
annotation class ZeroParamsAnnotation
annotation class ZeroParamsBracesAnnotation()

class A

@ZeroParamsAnnotation
@Annotation1("test1")
@ZeroParamsBracesAnnotation
fun test(param: Triple<Map<List<String>, Int>, Set<Byte>, A>) {

}

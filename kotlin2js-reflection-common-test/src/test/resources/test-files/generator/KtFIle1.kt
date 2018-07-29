annotation class Annotation1(val name: String)

class A

@Annotation1("test1")
fun test(param: Triple<Map<List<String>, Int>, Set<Byte>, A>) {

}

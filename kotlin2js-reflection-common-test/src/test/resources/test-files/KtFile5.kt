annotation class StringTemplate(val string: String)
annotation class MultiStringTemplate(val string: String)

@StringTemplate("""template""")
@MultiStringTemplate("""
    multistring-template
""")
fun main(args: Array<String>) {

}
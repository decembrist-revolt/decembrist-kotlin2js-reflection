# Decembrist Kotlin2Js Reflection 
[![Build Status](https://travis-ci.org/decembrist-revolt/decembrist-kotlin2js-reflection.svg?branch=master)](https://travis-ci.org/decembrist-revolt/decembrist-kotlin2js-reflection)

Plugin for kotlin js annotation processing

The plugin allows you to not be disappointed when you see the message "Unsupported [This reflection API is not supported yet in JavaScript]"

Plugin for the moment can process hider-ordered functions, classes and methods annotations.
### License
Apache 2.0
### Installation
For _MAVEN_:
pom.xml
```xml
...
<dependencies>
    <dependency>
        <groupId>org.decembrist</groupId>
        <artifactId>kotlin2js-reflection-api</artifactId>
        <version>0.1.0-beta-1</version>
    </dependency>
    <dependency>
        <groupId>org.jetbrains.kotlin</groupId>
        <artifactId>kotlin-stdlib-js</artifactId>
        <version>${kotlin.version}</version>
    </dependency>
</dependencies>
<build>
    <sourceDirectory>src/main/kotlin</sourceDirectory>
    <plugins>
        <plugin>
            <artifactId>kotlin-maven-plugin</artifactId>
            <groupId>org.jetbrains.kotlin</groupId>
            <version>${kotlin.version}</version>
            <executions>
                <execution>
                    <id>compile</id>
                    <phase>compile</phase>
                    <goals>
                        <goal>js</goal>
                    </goals>
                    <configuration>
                        <sourceDirs>
                            <sourceDir>src/main/kotlin</sourceDir>
                            <sourceDir>${build.directory}/generated-sources/decembrist</sourceDir>
                        </sourceDirs>
                        <outputFile>${project.build.outputDirectory}/${project.artifactId}.js</outputFile>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        <plugin>
            <groupId>org.decembrist</groupId>
            <artifactId>kotlin2js-reflection-mplugin</artifactId>
            <version>0.1.0-beta-1</version>
            <executions>
                <execution>
                    <goals>
                        <goal>process</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        <plugin>
            <artifactId>maven-dependency-plugin</artifactId>
            <executions>
                <!-- unpack kotlin.js to target/classes/lib for example index.html -->
                <execution>
                    <id>extract-kotlinjs</id>
                    <phase>prepare-package</phase>
                    <goals>
                        <goal>unpack-dependencies</goal>
                    </goals>
                    <configuration>
                        <includeArtifactIds>kotlin-stdlib-js</includeArtifactIds>
                        <outputDirectory>${build.outputDirectory}/lib</outputDirectory>
                        <includes>**\/*.js</includes>
                        <excludes>**\/*.meta.js</excludes>
                    </configuration>
                </execution>
                <!-- unpack kotlin2js-reflection-api.js to target/classes/lib for example index.html -->
                <execution>
                    <id>extract-reflection-api</id>
                    <phase>prepare-package</phase>
                    <goals>
                        <goal>unpack-dependencies</goal>
                    </goals>
                    <configuration>
                        <includeArtifactIds>kotlin2js-reflection-api</includeArtifactIds>
                        <outputDirectory>${build.outputDirectory}/lib</outputDirectory>
                        <includes>**\/*.js</includes>
                        <excludes>**\/*.meta.js
                        </excludes>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```  
<pom.xml directory>/index.html
```html
<!DOCTYPE html>
<html>
<head>
    <meta charset='utf-8'>
    <title>Test page</title>
    <!-- include kotlin runtime (run mvn package to get it there) -->
    <script type="text/javascript" src="target/classes/lib/kotlin.js"></script>
    <!-- include kotlin reflection api (run mvn package to get it there) order must be observed (after kotlin.js)  -->
    <script type="text/javascript" src="target/classes/lib/kotlin2js-reflection-api.js"></script>
    <!-- your compiled script (run mvn compile to get it up to date) -->
    <script type="text/javascript" src="target/classes/<your-file>.js"></script>
</head>
<body>
</body>
</html>
```
use _package_ maven task
For _GRADLE_:
build.gradle
```gradle
plugins {
    id 'kotlin2js' version '1.3.0'
    id 'org.decembrist.kotlin2js.reflection' version '0.1.0-beta-1'
}
group '<your-group>'
version '<your-version>'
repositories {
    mavenCentral()
}
dependencies {
    compile "org.decembrist:kotlin2js-reflection-api:0.1.0-beta-1"
    compile "org.jetbrains.kotlin:kotlin-stdlib-js"
}
kotlin2JsReflection {
    generatedSourcesDir = file("${project.buildDir}/generated/decembrist")
}
sourceSets.main.kotlin.srcDirs += kotlin2JsReflection.generatedSourcesDir
//unpack kotlin.js and reflection-api dependencies
task assembleWeb(type: Sync) {
    configurations.compile.each { File file ->
        from(zipTree(file.absolutePath), {
            includeEmptyDirs = false
            include { fileTreeElement ->
                def path = fileTreeElement.path
                path.endsWith(".js") && (path.startsWith("META-INF/resources/") ||
                        !path.startsWith("META-INF/"))
            }
        })
    }
    from compileKotlin2Js.destinationDir
    into "${projectDir}/web"

    dependsOn classes
}
assemble.dependsOn assembleWeb
```
settings.gralde
```gradle
pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "kotlin2js") {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            }
        }
    }
}
rootProject.name = <your project name>
```
<build.gradle directory>/index.html
```html
<!DOCTYPE html>
<html>
<head>
    <meta charset='utf-8'>
    <title>Test page</title>
    <!-- include kotlin runtime -->
    <script type="text/javascript" src="web/kotlin.js"></script>
    <!-- include kotlin reflection api, order must be observed (after kotlin.js)  -->
    <script type="text/javascript" src="web/kotlin2js-reflection-api.js"></script>
    <!-- your compiled script -->
    <script type="text/javascript" src="web/<your-file>.js"></script>
</head>
<body>
</body>
</html>
```
use _build_ gradle task
### Code example
```kotlin
import org.decembrist.utils.jsReflect
import kotlin.browser.document
import kotlin.browser.window

annotation class MyFirstAnnotation(val text: String)

@MyFirstAnnotation("test")
fun main() {
    val annotations = ::main.jsReflect.getAnnotations()
    val myFirstAnnotation = annotations.first { it is MyFirstAnnotation } as MyFirstAnnotation
    window.onload = {
        document.body!!.innerHTML = myFirstAnnotation.text
        ""
    }
}
```
Let's open index.html in browser
```
test
```
### Reflection-api
- KClass.jsReflect - extention property, returns _JsClassReflect_ type
```kotlin
val <T : Any> KClass<T>.jsReflect 
```
- KFunction.jsReflect - extention property, returns _JsFunctionReflect_ for hider-ordered function or _JsMethodReflect_ for method
```kotlin
val KFunction<*>.jsReflect
```
- JsClassReflect interface
```kotlin
    /**
     * Reflection data annotations
     */
    val annotations: List<Annotation>
    /**
     * @return class name from compiled js file
     */
    val jsName: String
    /**
     * @return class js constructor function
     */
    val jsConstructor: dynamic
    /**
     * @return class methods reflection data list
     */
    val methods: List<JsMethodReflect<T>>
    /**
     * Create a new class instance through js constructor
     */
    fun createInstance(arguments: Array<Any> = js("[]")): T
```
- JsFunctionReflect interface
```kotlin
    /**
     * Reflection data annotations
     */
    val annotations: List<Annotation>
    /**
    * Return function js name
    */
    val jsName: String
    /**
     * Get function annotations
     * @param kClass should be provided if this is method
     */
    fun getAnnotations(kClass: KClass<*>? = null): List<Annotation>
```
- JsMethodReflect interface
```kotlin
    /**
     * Reflection data annotations
     */
    val annotations: List<Annotation>
    /**
     * Return method name
     */
    val name: String
    /**
     * Invoke function with arguments
     *
     * @param receiver object
     * @param args arguments
     * @return function result
     */
    operator fun invoke(receiver: T, vararg args: Any): Any
```
Reflection object
```kotlin
    /**
     * Get annotations for class
     * @param kClass
     * @return annotations list
     */
    fun getAnnotations(kClass: KClass<*>): List<Annotation>
    /**
     * Get annotations for function or class method (if present)
     * @param kClass class
     * @param kFunction function or method
     * @return annotations list
     */
    fun getAnnotations(kFunction: KFunction<*>, kClass: KClass<*>? = null): List<Annotation>
    /**
     * Get methods for class
     * @param kClass
     * @return methods list
     */
    fun getMethods(kClass: KClass<*>): List<MethodInfo>
```
### Example
You can use our test as [example](https://github.com/decembrist-revolt/decembrist-kotlin2js-reflection/tree/master/kotlin2js-reflection-plugin-test/src/test/resources/published-test)
### Plugin configuration
TODO(sorry)
### Restrictions
The plugin works with raw code and therefore has some limitations at the moment.
- Annotations can be processed for public members only
- KotlinJs Annotations won't be processed (@JsName, @JsModule e.t.c)
- Only primitive type annotation params allowed (and arrays of them)
- _Cross-project annotations will be added in upcoming patches_

We plan to fix these limitations in the future.

### Tested annotation examples
```kotlin
annotation class Annotation(val name: String)
annotation class ZeroParamsAnnotation
annotation class ZeroParamsBracesAnnotation()

@ZeroParamsAnnotation
@Annotation("test")
@ZeroParamsBracesAnnotation
fun test() {

}
_____________________________
annotation class Annotation(val string: String,
                             val byte: Byte,
                             val short: Short,
                             val int: Int,
                             val long: Long,
                             val float: Float,
                             val double: Double,
                             val char: Char,
                             val bool: Boolean)

@Annotation("test1", 1, 1, 1, 1L, 1.0f, 1.0, 't', true)
fun main(args: Array<String>) {

}
___________________________
annotation class Annotation(val string: Array<String>,
                                 val byte: ByteArray,
                                 val short: ShortArray,
                                 val int: IntArray,
                                 val long: LongArray,
                                 val float: FloatArray,
                                 val double: DoubleArray,
                                 val char: CharArray,
                                 val bool: BooleanArray)

@Annotation(["test1", "test2"], [1, 2], [1, 2], [1, 2], [1L, 2L], [1.0f, 2.0f], [1.0, 2.0], ['1', '2'], [true, false])
fun main(args: Array<String>) {

}
___________________________
annotation class Annotation1(vararg val string: String)
annotation class Annotation2(vararg val byte: Byte)
annotation class Annotation3(vararg val short: Short)
annotation class Annotation4(vararg val int: Int)
annotation class Annotation5(vararg val long: Long)
annotation class Annotation6(vararg val float: Float)
annotation class Annotation7(vararg val double: Double)
annotation class Annotation8(vararg val char: Char)
annotation class Annotation9(vararg val bool: Boolean)

@Annotation1(*arrayOf("test1", "test2"))
@Annotation2(1, 2)
@Annotation3(1, 2)
@Annotation4(*[1, 2])
@Annotation5(*[1L, 2L])
@Annotation6(*[1.0f, 2.0f])
@Annotation7(*[1.0, 2.0])
@Annotation8(*['1', '2'])
@Annotation9(*[true, false])
fun main(args: Array<String>) {

}
__________________________
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
___________________________
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
___________________________
annotation class StringTemplate(val string: String)
annotation class MultiStringTemplate(val string: String)

@StringTemplate("""template""")
@MultiStringTemplate("""
    multistring-template
""")
fun main(args: Array<String>) {

}
```
### Community
Your issue tickets or any help will be very appreciated.

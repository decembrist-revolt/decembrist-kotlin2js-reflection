package org.decembrist.generators

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import org.decembrist.Message.nainClassNotFoundMessage
import org.decembrist.domain.content.KtFileContent
import java.util.*

class ReflectionUtilsGenerator(private val mainClass: String) {

    private val reflectionUUID = "r" + UUID.randomUUID().toString().replace("-", "")

    private val fileName = "reflection$reflectionUUID"

    fun generateCode(ktFileContents: List<KtFileContent>) {
        val mainClassFile = findMainClassFile(ktFileContents)
        val packageName = mainClassFile.`package`?.name ?: ""
        FileSpec
                .builder(packageName, fileName)
                .addProperty(generateReflectionObjProp())
    }

    private fun findMainClassFile(ktFileContents: List<KtFileContent>) = ktFileContents
            .firstOrNull { ktFileContent ->
                val packageName = if (ktFileContent.`package`?.hasPackage()!!) {
                    ktFileContent.`package`!!.name + "."
                } else ""
                val className = if (".kt" in ktFileContent.name) {
                    ktFileContent.name.replace(".kt", "")
                } else ktFileContent.name
                "$packageName$className" == mainClass
            } ?: throw IllegalArgumentException(nainClassNotFoundMessage(mainClass))

    fun generateReflectionObjProp() = PropertySpec
            .builder(reflectionUUID, STRING_TYPE, KModifier.PRIVATE)
            .mutable(false)
            .initializer("""js("{}")""")
            .build()

    companion object {
        val STRING_TYPE = ClassName("", "String")
    }

}
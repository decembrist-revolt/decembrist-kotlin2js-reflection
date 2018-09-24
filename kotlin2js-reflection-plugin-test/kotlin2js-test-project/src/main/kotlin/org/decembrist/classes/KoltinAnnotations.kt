package org.decembrist.classes

import org.decembrist.assertTrue
import org.decembrist.utils.jsReflect

@JsName("jsname")
class EmbeddedAnnotations

@JsNonModule
external class NonModule

fun checkKotlinAnnotations() {
    val jsName = EmbeddedAnnotations::class.jsReflect.annotations
            .first { it is JsName } as JsName
    assertTrue(jsName.name == "jsname")
    val jsNonModule = NonModule::class.jsReflect.annotations
            .firstOrNull { it is JsNonModule }
            ?.let { it as JsNonModule }
    assertTrue(jsNonModule != null)
}
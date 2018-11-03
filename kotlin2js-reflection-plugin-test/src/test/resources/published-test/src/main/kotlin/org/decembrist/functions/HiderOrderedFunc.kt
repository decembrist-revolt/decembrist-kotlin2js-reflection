package org.decembrist.functions

import org.decembrist.assertTrue
import org.decembrist.utils.jsReflect

@JsName("name")
fun hiderOrderFunction() = "some"

fun checkHiderOrderFunctionInvoke() {
    val function = ::hiderOrderFunction
    val some = function()
    assertTrue(some == "some")
    val nativeName = function.jsReflect.jsName
    assertTrue(nativeName == "hiderOrderFunction_0")
    js("function hiderOrderFunction(){}")
}
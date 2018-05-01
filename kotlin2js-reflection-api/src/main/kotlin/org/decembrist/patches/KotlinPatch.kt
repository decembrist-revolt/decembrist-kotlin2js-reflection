package org.decembrist.patches

import org.decembrist.utils.REFLECTION_INFO

fun main(args: Array<String>) {
    eval("""
        var getKClass_44706f08_4987_11e8_842f_0ed5f89f718b = Kotlin.getKClass;
        Kotlin.getKClass = function(jClass){
            var kClass = getKClass_44706f08_4987_11e8_842f_0ed5f89f718b(jClass);
            kClass["$REFLECTION_INFO"] = {};
            kClass["$REFLECTION_INFO"].jsName = jClass.name;
            kClass["$REFLECTION_INFO"].jsConstructor = jClass;
            kClass["$REFLECTION_INFO"].createInstance = function(args){
                var object = Object.create(jClass.prototype);
                jClass.apply(object, args);
                return object;
            }
            return kClass;
        }
    """)
}

//internal val patchGetKClassFunction = js("""
//    eval("var getKClass_44706f08_4987_11e8_842f_0ed5f89f718b = Kotlin.getKClass;Kotlin.getKClass = function(jClass){var kClass = getKClass_44706f08_4987_11e8_842f_0ed5f89f718b(jClass); kClass.jsName = jClass.name; return kClass;}")
//""")
package org.decembrist.model

import kotlin.reflect.KFunction

/**
 * Function identifier representation
 */
class FunctionIdentifier(val name: String,
                              val body: String,
                              val function: KFunction<*>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FunctionIdentifier) return false

        if (name != other.name) return false
        if (body != other.body) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + body.hashCode()
        return result
    }

}
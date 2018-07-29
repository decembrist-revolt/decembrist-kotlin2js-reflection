package org.decembrist.common

import java.util.Collections.singletonList
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaGetter

object FunctionUtils {

    fun parseFunction(function: String): Function {
        val name = function.substringBefore("(").split(".").component2()
        val paramsSubstring = function.substring(
                function.indexOfFirst { it == '(' } + 1,
                function.indexOfLast { it == ')' }
        )
        val parameters = parseParameters(paramsSubstring)
        val returnTypeString = function.substringAfterLast(":").trim()
        val returnType = parseType(returnTypeString)
        return Function(name, parameters, returnType)
    }

    private fun parseParameters(params: String): List<Type> {
        var bracketsCount = 0
        val charArray = params.toCharArray()
        val commaIndexes = mutableListOf<Int>()
        if (params.contains(",")) {
            for (index in charArray.indices) {
                val symbol = charArray[index]
                if (symbol == '<') {
                    bracketsCount++
                } else if (symbol == '>') {
                    bracketsCount--
                } else if (symbol == ',' && bracketsCount == 0) {
                    commaIndexes.add(index)
                }
            }
        }
        return if (commaIndexes.isEmpty()) {
            val type = parseType(params)
            singletonList(type)
        } else {
            var startIndex = 0
            var endIndex: Int
            val paramsList = mutableListOf<String>()
            for (commaIndex in commaIndexes) {
                endIndex = commaIndex
                val param = params.substring(startIndex, endIndex)
                paramsList.add(param)
                startIndex = endIndex + 1
            }
            endIndex = params.lastIndex + 1
            val param = params.substring(startIndex, endIndex)
            paramsList.add(param)
            paramsList.map(::parseType)
        }
    }

    private fun parseType(param: String): Type {
        return if (param.contains("<")) {
            val clazz = param.substringBefore("<").trim()
            val subTypes = param.substring(
                    param.indexOfFirst { it == '<' } + 1,
                    param.indexOfLast { it == '>' }
            ).let(::parseParameters)
            Type(clazz, subTypes)
        } else {
            Type(param.trim())
        }
    }

    class Function(val name: String, val paramTypes: List<Type>, val returnType: Type)

    class Type(val clazz: String, val subTypes: List<Type> = emptyList())

}
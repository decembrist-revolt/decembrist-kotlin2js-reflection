package org.decembrist.fillers

import org.decembrist.domain.content.KtFileContent
import org.decembrist.domain.content.functions.FunctionParameter
import org.decembrist.domain.content.functions.IFuncContent
import org.decembrist.domain.content.members.IMembered
import org.decembrist.domain.content.members.Method
import org.decembrist.services.TypeService.splitFullClassName
import org.decembrist.services.typesuggestions.TypeSuggestion
import org.decembrist.services.typesuggestions.Projection
import org.decembrist.services.typesuggestions.TypeSuggestion.*
import org.decembrist.services.typesuggestions.UnknownProjection
import java.util.Collections.singletonList

class FunctionFiller(val fileContents: Collection<KtFileContent>) {

    //TODO retrieve all classes by package pair
    private val classItemList: List<ClassItem>

    init {
        classItemList = fileContents
                .map { fileContent ->
                    val packageName = fileContent.`package`?.name ?: ""
                    return@map fileContent.classes
                            .map { ClassItem(it.name, packageName) }
                }.flatten()
    }

    /**
     * Fill missing annotation instance info from [fileContent]
     */
    fun fill(): Collection<KtFileContent> {
        for (fileContent in fileContents) {
            val methods = fileContent.classes
                    .filter { it is IMembered }
                    .map { (it as IMembered).methods }
                    .flatten()
            val unknownTypeFunctions = methods.filter { hasUnknownTypes(it) }
            val packageName = fileContent.`package`?.name ?: ""
            for (function in unknownTypeFunctions) {
                replaceUnknownParameters(function, packageName)
                replaceUnknownReturnType(function, packageName)
            }
        }
        return fileContents
    }

    private fun hasUnknownTypes(function: IFuncContent): Boolean {
        val parameterTypes = function.functionParameters
                .map { it.type }
        return parameterTypes
                .plus(function.returnType)
                .any { hasUnknownTypes(singletonList(it)) }
    }

    private fun hasUnknownTypes(types: List<TypeSuggestion>): Boolean {
        return types.any { type ->
            return@any when (type) {
                is Unknown -> true
                is UnknownProjection -> true
                is Projection -> hasUnknownTypes(type.projections)
                else -> false
            }
        }
    }

    private fun replaceUnknownParameters(function: IFuncContent, packageName: String) {
        val newParams = function.functionParameters
                .map { parameter ->
                    val type = parameter.type
                    val newType = replaceUnknownType(type, packageName)
                    return@map FunctionParameter(
                            parameter.name,
                            newType,
                            parameter.defaultValue
                    )
                }
        function.functionParameters = newParams
    }

    private fun replaceUnknownReturnType(function: Method, packageName: String) {
        if (function.returnType is Unknown) {
            replaceUnknownType(function.returnType, packageName)
        }
    }

    private fun replaceUnknownType(type: TypeSuggestion, packageName: String): TypeSuggestion {
        val classItem = if (type is Unknown) {
            classItemList
                    .filter { classItem -> classItem.packageName == packageName }
                    .firstOrNull { classItem -> classItem.className == type.type }
                    ?: ClassItem.of(type.type)
        } else ClassItem.of(type.type)

        return if (type is AbstractProjection) {
            val projections = type.projections
                    .map { replaceUnknownType(it, packageName) }
            Projection(
                    classItem.className,
                    type.nullable,
                    classItem.packageName,
                    type.`in`,
                    type.out,
                    projections)
        } else {
            Type(
                    classItem.className,
                    classItem.packageName,
                    type.nullable)
        }
    }

    private class ClassItem(val className: String,
                            val packageName: String) {

        //        override fun toString(): String {
//            return "@${concatenateClassName(annotationName, packageName)}" +
//                    "(${parameters.joinToString()})"
//        }

        companion object {

            fun of(fullType: String): ClassItem {
                val (className, packageName) = splitFullClassName(fullType)
                return ClassItem(className, packageName)
            }

        }

    }

}
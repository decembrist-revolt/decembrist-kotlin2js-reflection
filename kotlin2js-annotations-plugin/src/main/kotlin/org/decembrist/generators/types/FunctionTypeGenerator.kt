package org.decembrist.generators.types

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeName
import org.decembrist.domain.content.members.Method
import org.decembrist.generators.IGenerator

class FunctionTypeGenerator(val clazz: TypeName): IGenerator<Method> {

    override fun generate(content: Method): CodeBlock {
        val paramsCount = content.functionParameters.size + 1
        val kFunctionType = ClassName(
                KFUNCTION_PACKAGE_NAME,
                "KFUNCTION_TYPE_NAME$paramsCount"
        )
        return CodeBlock.of("")
    }

    private fun retrieveParametrized(type: TypeName,
                                     subTypes: List<TypeName>){

    }

    companion object {

        const val KFUNCTION_TYPE_NAME = "KFunction"
        const val KFUNCTION_PACKAGE_NAME = "kotlin.reflect"

    }

}
package org.decembrist.services

import com.github.sarahbuisson.kotlinparser.KotlinParser.*
import org.decembrist.domain.Import
import org.decembrist.domain.content.functions.FunctionParameter
import org.decembrist.domain.modifiers.ClassModifiers
import org.decembrist.domain.modifiers.FunctionModifiers
import org.decembrist.resolvers.AnnotationInstanceContextResolver
import org.decembrist.services.Modifier.*
import org.decembrist.services.TypeService.getTypeSuggestion
import org.decembrist.services.typesuggestions.TypeSuggestion

object RuleContextService {

    fun getAnnotations(ctx: FunctionDeclarationContext,
                       imports: Collection<Import>) = ctx.modifierList()
            ?.annotations()
            .orEmpty()
            .map { it.annotation() }
            .let { AnnotationInstanceContextResolver.fromList(it, imports) }

    fun getAnnotations(ctx: ClassDeclarationContext,
                       imports: Collection<Import>) = ctx.modifierList()
            ?.annotations()
            .orEmpty()
            .map { it.annotation() }
            .let { AnnotationInstanceContextResolver.fromList(it, imports) }

    fun modifierExists(modifiers: MutableList<ModifierContext>?,
                       modifier: Modifier): Boolean {
        return modifiers?.any { it.text == modifier.modifierName } == true
    }

    fun getClassModifiers(ctx: ClassDeclarationContext): ClassModifiers {
        val modifiers = ctx.modifierList()?.modifier()
        val (isAbstract, isFinal, isOpen) = getEntityModifiers(modifiers)
        val isData = modifierExists(modifiers, DATA_MODIFIER)
        val isInner = modifierExists(modifiers, INNER_MODIFIER)
        val isSealed = modifierExists(modifiers, SEALED_MODIFIER)
        return ClassModifiers(
                isAbstract,
                isFinal,
                isOpen,
                isData,
                isInner,
                isSealed
        )
    }

    fun getFunctionModifiers(ctx: FunctionDeclarationContext): FunctionModifiers {
        val modifiers = ctx.modifierList()?.modifier()
        val (isAbstract, isFinal, isOpen) = getEntityModifiers(modifiers)
        val isExternal = modifierExists(modifiers, EXTERNAL_MODIFIER)
        val isInfix = modifierExists(modifiers, INNER_MODIFIER)
        val isInline = modifierExists(modifiers, INLINE_MODIFIER)
        val isOperator = modifierExists(modifiers, OPERATOR_MODIFIER)
        val isSuspend = modifierExists(modifiers, SUSPEND_MODIFIER)
        return FunctionModifiers(
                isAbstract,
                isFinal,
                isOpen,
                isExternal,
                isInfix,
                isInline,
                isOperator,
                isSuspend
        )
    }

    fun retrieveFunctionParameters(ctx: FunctionDeclarationContext,
                                   imports: Collection<Import>): List<FunctionParameter> {
        val functionValueParameters = ctx.functionValueParameters()
                ?.functionValueParameter()
                .orEmpty()
        return if (functionValueParameters.isNotEmpty()) {
            functionValueParameters.map {parameter ->
                val parameterCtx = parameter.parameter()
                return@map retrieveParameter(parameterCtx, imports)
            }
        } else emptyList()
    }

    fun retrieveFunctionReturnType(ctx: FunctionDeclarationContext,
                                   imports: Collection<Import>): TypeSuggestion {
        val explicitReturnType = ctx.children.contains(ctx.COLON())
        return if (explicitReturnType) {
            val indexOfColon = ctx.children.indexOf(ctx.COLON())
            val returnTypeIndex = indexOfColon + 1
            val returnType = ctx.children[returnTypeIndex] as TypeContext
            getTypeSuggestion(returnType, imports)
        } else TypeSuggestion.Type("Unit", "kotlin")
    }

    fun getClassName(ctx: ClassDeclarationContext) = ctx.simpleIdentifier().text

    fun getMemberOwnerClassName(ctx: ClassMemberDeclarationContext) = ctx.parent
            .parent
            .let { it as ClassDeclarationContext }
            .let { getClassName(it) }

    /**
     * TODO default value
     */
    private fun retrieveParameter(ctx: ParameterContext,
                                  imports: Collection<Import>): FunctionParameter {
        val name = ctx.simpleIdentifier().text
        val className = ctx.type().text
        val fullClass = ImportService.retrieveFullClass(imports, className)
        val type = TypeService.getTypeSuggestion(ctx.type(), imports)
        return FunctionParameter(name, type, null)
    }

    /**
     * @return triple (isAbstract, isFinal, isOpen) modifiers
     */
    private fun getEntityModifiers(
            modifiers: MutableList<ModifierContext>?): Triple<Boolean, Boolean, Boolean> {
        return if (modifiers == null) {
            Triple(false, false, false)
        } else {
            val isAbstract = modifierExists(modifiers, ABSTRACT_MODIFIER)
            val isFinal = modifierExists(modifiers, FINAL_MODIFIER)
            val isOpen = modifierExists(modifiers, OPEN_MODIFIER)
            Triple(isAbstract, isFinal, isOpen)
        }
    }

}
package org.decembrist.services

import org.antlr.v4.runtime.RuleContext
import org.decembrist.domain.Import
import org.decembrist.domain.content.annotations.AnnotationParameter
import org.decembrist.domain.content.functions.FunctionParameter
import org.decembrist.domain.modifiers.ClassModifiers
import org.decembrist.domain.modifiers.FunctionModifiers
import org.decembrist.parser.KotlinParser
import org.decembrist.parser.KotlinParser.*
import org.decembrist.resolvers.AnnotationInstanceContextResolver
import org.decembrist.services.Modifier.*
import org.decembrist.services.TypeService.getTypeSuggestion
import org.decembrist.services.typecontexts.VarargsType
import org.decembrist.services.typesuggestions.StarType
import org.decembrist.services.typesuggestions.TypeSuggestion
import kotlin.reflect.KVisibility

object RuleContextService {

    fun getAnnotations(ctx: FunctionDeclarationContext,
                       imports: Collection<Import>) = ctx.modifiers()
            ?.annotation()
            .orEmpty()
            .let { AnnotationInstanceContextResolver.fromList(it, imports) }

    fun getAnnotations(ctx: ClassDeclarationContext,
                       imports: Collection<Import>) = ctx.modifiers()
        ?.annotation()
        .orEmpty()
        .let { AnnotationInstanceContextResolver.fromList(it, imports) }

    fun getAnnotations(ctx: ObjectDeclarationContext,
                       imports: Collection<Import>) = ctx.modifiers()
        ?.annotation()
        .orEmpty()
        .let { AnnotationInstanceContextResolver.fromList(it, imports) }

    fun modifierExists(modifiers: MutableList<ModifierContext>?,
                       modifier: Modifier): Boolean {
        return modifiers?.any { it.text == modifier.modifierName } == true
    }

    fun getModifiers(modifiers: KotlinParser.ModifiersContext?): ClassModifiers {
        val modifier = modifiers?.modifier()
        val (isAbstract, isFinal, isOpen) = getEntityModifiers(modifier)
        val isData = modifierExists(modifier, DATA_MODIFIER)
        val isInner = modifierExists(modifier, INNER_MODIFIER)
        val isSealed = modifierExists(modifier, SEALED_MODIFIER)
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
        val modifiers = ctx.modifiers()?.modifier()
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
            functionValueParameters.map { parameter ->
                val parameterCtx = parameter.parameter()
                return@map retrieveParameter(parameterCtx, imports)
            }
        } else emptyList()
    }

    fun retrieveParameters(primaryConstructor: PrimaryConstructorContext?,
                           imports: Collection<Import>): List<AnnotationParameter> {
        return primaryConstructor
                ?.classParameters()
                ?.classParameter()
                ?.map { retrieveParameter(it, imports) } ?: emptyList()
    }

    fun retrieveFunctionReturnType(ctx: FunctionDeclarationContext,
                                   imports: Collection<Import>): TypeSuggestion {
        val explicitReturnType = ctx.children.contains(ctx.COLON())
        val implicitReturnType = explicitReturnType.not() && ctx.functionBody().ASSIGNMENT() != null
        return when {
            explicitReturnType -> {
                val indexOfColon = ctx.children.indexOf(ctx.COLON())
                val returnTypeIndex = indexOfColon + 1
                val returnType = ctx.children[returnTypeIndex] as TypeContext
                getTypeSuggestion(returnType, imports)
            }
            implicitReturnType -> StarType()
            else -> TypeSuggestion.Type("Unit", "kotlin")
        }
    }

    fun getClassName(ctx: ClassDeclarationContext) = ctx.simpleIdentifier().text
    fun getClassName(ctx: ObjectDeclarationContext) = ctx.simpleIdentifier().text

    fun getMemberOwnerClassName(ctx: ClassMemberDeclarationContext): String {
        tailrec fun findClassOrObjectDeclarationContextName(ctx: RuleContext): String = when(ctx) {
            is ClassDeclarationContext -> getClassName(ctx)
            is ObjectDeclarationContext -> getClassName(ctx)
                else -> {
                    val parent = ctx.parent ?: throw RuntimeException("missing class declaration $ctx")
                    findClassOrObjectDeclarationContextName(parent)
                }
        }
        return findClassOrObjectDeclarationContextName(ctx)
    }

    fun getVisibility(modifiers: ModifiersContext?): KVisibility {
        return modifiers
                ?.modifier()
                ?.mapNotNull { it.visibilityModifier() }
                ?.firstOrNull()
                ?.let { modifier ->
                    return@let when {
                        modifier.INTERNAL() != null -> KVisibility.INTERNAL
                        modifier.PRIVATE() != null -> KVisibility.PRIVATE
                        modifier.PROTECTED() != null -> KVisibility.PROTECTED
                        else -> KVisibility.PUBLIC
                    }
                } ?: KVisibility.PUBLIC
    }

    /**
     * TODO default value
     */
    private fun retrieveParameter(ctx: ParameterContext,
                                  imports: Collection<Import>): FunctionParameter {
        val name = ctx.simpleIdentifier().text
        val type = if (checkVarargs(ctx)) {
            val varargsType = VarargsType.of(ctx.type())
            TypeService.getTypeSuggestion(varargsType, imports)
        } else TypeService.getTypeSuggestion(ctx.type(), imports)
        return FunctionParameter(name, type, null)
    }

    /**
     * TODO default value
     */
    private fun retrieveParameter(ctx: ClassParameterContext,
                                  imports: Collection<Import>): AnnotationParameter {
        val name = ctx.simpleIdentifier().text
        val type = if (checkVarargs(ctx)) {
            val varargsType = VarargsType.of(ctx.type())
            TypeService.getTypeSuggestion(varargsType, imports)
        } else TypeService.getTypeSuggestion(ctx.type(), imports)
        return AnnotationParameter(name, type, null)
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

    private fun checkVarargs(ctx: ParameterContext): Boolean {
        return (ctx.parent as FunctionValueParameterContext)
                .modifiers()
                ?.modifier()
                .orEmpty()
                .any { it?.parameterModifier()?.VARARG() != null }
    }

    private fun checkVarargs(ctx: ClassParameterContext): Boolean {
        return ctx.modifiers()
                ?.modifier()
                .orEmpty()
                .any { it?.parameterModifier()?.VARARG() != null }
    }

}
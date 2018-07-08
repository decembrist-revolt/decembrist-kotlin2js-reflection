package org.decembrist.parsers

import com.github.sarahbuisson.kotlinparser.KotlinParser.*
import org.antlr.v4.runtime.RuleContext
import org.decembrist.domain.content.KtFileContent
import org.decembrist.domain.content.classes.Class
import org.decembrist.resolvers.ImportContextResolver
import org.decembrist.resolvers.PackageContextResolver
import org.decembrist.resolvers.factories.ClassContextResolverFactory
import org.decembrist.resolvers.factories.FunctionContextResolverFactory
import org.decembrist.resolvers.factories.MemberContextResolverFactory
import org.decembrist.services.ExceptionService.throwUnsupportedAnnotatedTypeException
import org.decembrist.services.RuleContextService.getAnnotations
import org.decembrist.services.RuleContextService.getMemberOwnerClassName

class KtFileListener(private val fileName: String) : ParserLibFixListener() {

    val fileContent = KtFileContent(fileName)

    private val imports
        get() = fileContent.imports!!.imports

    private val functionContextResolverFactory by lazy {
        FunctionContextResolverFactory(imports)
    }

    override fun enterPackageHeader(ctx: PackageHeaderContext) {
        fileContent.`package` = PackageContextResolver().resolve(ctx)
    }

    override fun exitImportList(ctx: ImportListContext) {
        fileContent.imports = ImportContextResolver().resolve(ctx)
    }

    /**
     * Retrieves function info and annotations
     *
     * @throws [UnsupportedOperationException] on unsupported annotated type
     */
    override fun enterFunctionDeclaration(ctx: FunctionDeclarationContext) {
        super.enterFunctionDeclaration(ctx)
        if (!isClassMember(ctx) and !isExtensionFunction(ctx)) {
            val annotations = getAnnotations(ctx, fileContent.imports!!.imports)
            try {
                val functionListener = functionContextResolverFactory
                        .getResolver(ctx)
                val function = functionListener.resolve(ctx).apply {
                    this.annotations += annotations
                }
                fileContent.functions += function
            } catch (ex: UnsupportedOperationException) {
                if (annotations.isNotEmpty()) {
                    throwUnsupportedAnnotatedTypeException(ctx, fileName)
                }
            }
        }
    }

    /**
     * Retrieves function info and annotations
     */
    override fun enterClassDeclaration(ctx: ClassDeclarationContext) {
        if (classValidation(ctx)) {
            val annotations = getAnnotations(ctx, imports)
            val resolver = ClassContextResolverFactory
                    .createInstance(imports)
                    .getResolver(ctx)
            val `class` = resolver.resolve(ctx).apply {
                this.annotations += annotations
            }
            fileContent.classes += `class`
        }
    }

    override fun enterClassMemberDeclaration(ctx: ClassMemberDeclarationContext) {
        try {
            val resolver = MemberContextResolverFactory
                    .createInstance(imports)
                    .getResolver(ctx)
            val member = resolver.resolve(ctx)
            val ownerName = getMemberOwnerClassName(ctx)
            val `class` = findClassContent(ownerName)
            `class`.addMember(member)
        } catch (ex: UnsupportedOperationException) {

        }
    }

    private fun addFunction(ctx: FunctionDeclarationContext) {

    }

    private fun findClassContent(className: String): Class {
        return fileContent.classes
                .asReversed()
                .filter { it is Class }
                .first { it.name == className } as Class
    }

    private fun isClassMember(ctx: RuleContext): Boolean {
        return ctx.parent is ClassMemberDeclarationContext
    }

    private fun isExtensionFunction(ctx: FunctionDeclarationContext) = ctx.DOT() != null

}
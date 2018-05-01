package org.decembrist.parsers

import com.github.sarahbuisson.kotlinparser.KotlinParser.*
import com.github.sarahbuisson.kotlinparser.KotlinParserBaseListener
import org.decembrist.domain.content.KtFileContent
import org.decembrist.domain.headers.annotations.AnnotationInstance
import org.decembrist.resolvers.ImportContextResolver
import org.decembrist.resolvers.PackageContextResolver
import org.decembrist.resolvers.factories.ClassContextResolverFactory
import org.decembrist.resolvers.factories.FunctionContextResolverFactory
import org.decembrist.services.ExceptionService.throwUnsupportedAnnotatedTypeException
import org.decembrist.services.RuleContextService.getAnnotations

class KtFileListener(private val fileName: String) : KotlinParserBaseListener() {

    val fileContent = KtFileContent(fileName)

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
        val annotations = getAnnotations(ctx, fileContent.imports!!.imports)
        try {
            val functionListener = FunctionContextResolverFactory.getResolver(ctx)
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

    override fun enterClassDeclaration(ctx: ClassDeclarationContext) {
        val imports = fileContent.imports!!.imports
        val annotations = getAnnotations(ctx, imports)
        val resolver = ClassContextResolverFactory.createInstance(imports).getResolver(ctx)
        val `class` = resolver.resolve(ctx).apply {
            this.annotations += annotations
        }
        fileContent.classes += `class`
    }

    override fun enterFunctionTypeReceiver(ctx: FunctionTypeReceiverContext?) {
        super.enterFunctionTypeReceiver(ctx)
    }

}
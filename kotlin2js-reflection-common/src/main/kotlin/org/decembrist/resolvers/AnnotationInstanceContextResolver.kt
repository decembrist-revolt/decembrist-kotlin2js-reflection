package org.decembrist.resolvers

import com.github.sarahbuisson.kotlinparser.KotlinParser
import com.github.sarahbuisson.kotlinparser.KotlinParser.AnnotationContext
import org.decembrist.domain.Import
import org.decembrist.domain.headers.annotations.AnnotationInstance
import org.decembrist.services.AnnotationService.retrieveAttribute
import org.decembrist.services.ImportService.findConnectedImport
import org.decembrist.services.TypeService.getTypeSuggestion

class AnnotationInstanceContextResolver(private val imports: Collection<Import>)
    : IContextResolver<AnnotationContext, AnnotationInstance> {

    override fun resolve(ctx: AnnotationContext): AnnotationInstance {
        val annotationName = ctx.LabelReference().text.removePrefix("@")
        val import = findConnectedImport(imports, annotationName)
        val type = if (import != null && annotationName == import) {
            getTypeSuggestion(annotationName, "")
        } else if (import != null) {
            getTypeSuggestion(import)
        } else getTypeSuggestion(annotationName)
        return AnnotationInstance(type).apply {
            attributes = retrieveAttributes(ctx)
        }
    }

    private fun retrieveAttributes(ctx: AnnotationContext) = ctx.valueArguments()
            ?.valueArgument()
            ?.map { retrieveAttribute(it) } ?: emptyList()

    companion object {

        /**
         * Convert context list to [AnnotationInstance] list
         */
        fun fromList(ctxList: Collection<KotlinParser.AnnotationContext>,
                     imports: Collection<Import>): List<AnnotationInstance> {
            val annotationListener = AnnotationInstanceContextResolver(imports)
            return ctxList.map { annotationListener.resolve(it) }
        }
    }

}
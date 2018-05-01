package org.decembrist.resolvers

import com.github.sarahbuisson.kotlinparser.KotlinParser
import com.github.sarahbuisson.kotlinparser.KotlinParser.AnnotationContext
import org.decembrist.domain.Import
import org.decembrist.domain.headers.annotations.AnnotationInstance
import org.decembrist.services.AnnotationService.retrieveAttribute
import org.decembrist.services.ImportService
import org.decembrist.services.TypeService

class AnnotationInstanceContextResolver(private val imports: Collection<Import>)
    : IContextResolver<AnnotationContext, AnnotationInstance> {

    override fun resolve(ctx: AnnotationContext): AnnotationInstance {
        val annotationName = ctx.LabelReference().text.removePrefix("@")
        val annotationClass = ImportService.findFullClass(imports, annotationName)
        val typeName = TypeService.getTypeName(annotationClass)
        val annotationInstance = AnnotationInstance(typeName)
        annotationInstance.attributes.addAll(retrieveAttributes(ctx))
        return annotationInstance
    }

    private fun retrieveAttributes(ctx: AnnotationContext) = ctx.valueArguments()
                .valueArgument()
                .map { retrieveAttribute(it) }

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
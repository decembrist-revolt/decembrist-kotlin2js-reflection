package org.decembrist.classes

import org.decembrist.assertTrue
import org.decembrist.utils.jsReflect

annotation class AnnotationAnnotation

@AnnotationAnnotation
annotation class AnnotationForAnnotation

fun checkAnnotationAnnotation() {
    val annotations = AnnotationForAnnotation::class.jsReflect.annotations
    val annotationAnnotation = annotations
            .firstOrNull { it is AnnotationAnnotation }
            ?.let { it as AnnotationAnnotation }
    assertTrue(annotationAnnotation != null)
    assertTrue(annotations.size == 1)
}
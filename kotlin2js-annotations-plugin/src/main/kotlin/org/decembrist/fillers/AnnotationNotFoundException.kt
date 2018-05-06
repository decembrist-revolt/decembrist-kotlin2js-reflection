package org.decembrist.fillers

class AnnotationNotFoundException(annotation: String)
    : RuntimeException("Annotation @$annotation not found in classpath and won't be processed")
package org.decembrist.fillers.exceptions

class AnnotationNotFoundException(annotation: String)
    : RuntimeException("Annotation @$annotation not found in classpath and won't be processed")
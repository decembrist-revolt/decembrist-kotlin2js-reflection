package org.decembrist.fillers.exceptions

class PrivateAnnotationException(annotation: String)
    : RuntimeException("Annotation @$annotation is private and won't be processed")
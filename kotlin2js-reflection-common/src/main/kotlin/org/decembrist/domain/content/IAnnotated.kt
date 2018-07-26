package org.decembrist.domain.content

import org.decembrist.domain.headers.annotations.AnnotationInstance

interface IAnnotated: IContent {

    var annotations: Set<AnnotationInstance>

}
package org.decembrist.domain.content.members

import org.decembrist.domain.content.functions.Method

interface IMethoded {

    val methods: MutableSet<Method>

}
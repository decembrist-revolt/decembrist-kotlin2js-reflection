package org.decembrist.domain

import com.squareup.kotlinpoet.TypeName

data class Attribute(val name: String,
                     val value: String,
                     val type: TypeName?)
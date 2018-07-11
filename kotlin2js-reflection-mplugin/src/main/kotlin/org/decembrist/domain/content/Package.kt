package org.decembrist.domain.content

class Package(val name: String) : IContent {

    fun hasPackage() = !name.isBlank()
}
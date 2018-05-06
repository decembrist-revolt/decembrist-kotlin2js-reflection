package org.decembrist.domain.content.members

interface IMembered {

    val methods: MutableSet<Method>

    val fields: MutableSet<Field>

    fun addMember(member: IMember) {
        when (member) {
            is Method -> methods += member
            is Field -> fields += member
            else -> UnsupportedOperationException("Member ${member::class} unsupported yet")
        }
    }

}
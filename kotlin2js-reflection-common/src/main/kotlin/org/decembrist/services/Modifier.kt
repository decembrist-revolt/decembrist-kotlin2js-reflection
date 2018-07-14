package org.decembrist.services

enum class Modifier(val modifierName: String) {

    ANNOTATION_MODIFIER("annotation"),
    ABSTRACT_MODIFIER("abstract"),
    DATA_MODIFIER("abstract"),
    FINAL_MODIFIER("abstract"),
    INNER_MODIFIER("inner"),
    OPEN_MODIFIER("open"),
    SEALED_MODIFIER("sealed"),
    INFIX_MODIFIER("infix"),
    INLINE_MODIFIER("inline"),
    OPERATOR_MODIFIER("operator"),
    SUSPEND_MODIFIER("suspend"),
    EXTERNAL_MODIFIER("external")

}
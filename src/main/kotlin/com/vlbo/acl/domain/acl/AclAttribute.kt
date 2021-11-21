package com.vlbo.acl.domain.acl

enum class AclAttribute(private val attributeName: String) {

    GROUP("GROUP");

    open fun getSidForAttributeValue(value: String): String {
        return getSidPrefix() + value
    }

    open fun getSidPrefix(): String {
        return "ATTR:${attributeName.uppercase()}="
    }
}
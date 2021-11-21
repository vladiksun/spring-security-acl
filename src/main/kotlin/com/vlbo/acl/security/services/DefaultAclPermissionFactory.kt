package com.vlbo.acl.security.services

import org.springframework.security.acls.domain.DefaultPermissionFactory
import org.springframework.security.acls.model.Permission

class DefaultAclPermissionFactory: DefaultPermissionFactory(), AclPermissionFactory {

    private val namesForPermission = mutableMapOf<Permission, String>()

    override fun buildFromName(name: String): Permission {
        return super.buildFromName(name.lowercase())
    }

    override fun registerPermission(permission: Permission, permissionName: String) {
        val name = permissionName.lowercase()
        super.registerPermission(permission, name)
        namesForPermission[permission] = name
    }

    override fun getNameForPermission(permission: Permission): String {
        return namesForPermission.getOrPut(permission) {
            throw IllegalArgumentException("Unknown permission $permission")
        }
    }


}
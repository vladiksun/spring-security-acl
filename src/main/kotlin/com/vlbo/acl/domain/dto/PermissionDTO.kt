package com.vlbo.acl.domain.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.vlbo.acl.security.services.AclPermission
import org.springframework.security.acls.model.Permission
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class PermissionDTO {

    @NotBlank @NotNull lateinit var name:  String
    lateinit var permissionCodes: Array<Char>

    @get:JsonIgnore
    val mappedPermissions: Map<Char, AclPermission>
        get() {
            val permissions = mutableMapOf<Char, AclPermission>()

            for (permissionCode in permissionCodes) {
                when (permissionCode) {
                    'R' -> permissions[permissionCode] = AclPermission.READ
                    'W' -> permissions[permissionCode] = AclPermission.WRITE
                    'D' -> permissions[permissionCode] = AclPermission.DELETE
                    'A' -> permissions[permissionCode] = AclPermission.ADMINISTRATION
                    else -> {}
                }
            }
            return permissions
        }

    @get:JsonIgnore
    val permissions: Array<AclPermission>
        get() {
            val permissions = mutableSetOf<AclPermission>()

            for (permissionCode in permissionCodes) {
                when (permissionCode) {
                    'R' -> permissions.add(AclPermission.READ)
                    'W' -> permissions.add(AclPermission.WRITE)
                    'D' -> permissions.add(AclPermission.DELETE)
                    'A' -> permissions.add(AclPermission.ADMINISTRATION)
                    else -> {}
                }
            }
            return permissions.toTypedArray()
        }
}
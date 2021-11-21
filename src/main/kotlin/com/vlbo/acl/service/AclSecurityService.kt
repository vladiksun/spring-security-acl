package com.vlbo.acl.service

import com.vlbo.acl.security.services.AclPermission
import org.springframework.security.acls.model.AuditableAcl
import org.springframework.security.core.userdetails.UserDetails

interface AclSecurityService {

    fun grantPermissionsToUser(
        principal: UserDetails,
        type: Class<*>,
        id: Long?,
        aclPermissions: Array<AclPermission>
    ): AuditableAcl

    fun grantPermissionsToSid(
        sidName: String,
        type: Class<*>,
        id: Long?,
        aclPermissions: Array<AclPermission>
    ): AuditableAcl

}
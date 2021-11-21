package com.vlbo.acl.service

import com.vlbo.acl.services.AclPermission
import org.springframework.security.acls.model.AuditableAcl
import org.springframework.security.core.userdetails.UserDetails

interface AclSecurityService {

    fun grantPermissionsToUser(
        principal: UserDetails,
        type: Class<*>,
        id: Long?,
        aclPermissions: Array<AclPermission>
    ): AuditableAcl

}
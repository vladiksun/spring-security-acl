package com.vlbo.acl.service

import com.vlbo.acl.security.services.AclPermission
import org.springframework.security.acls.domain.GrantedAuthoritySid
import org.springframework.security.acls.domain.ObjectIdentityImpl
import org.springframework.security.acls.domain.PrincipalSid
import org.springframework.security.acls.model.*
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import java.io.Serializable

@Service
class AclSecurityServiceImpl(val aclService: MutableAclService): AclSecurityService {

    override fun grantPermissionsToUser(
        principal: UserDetails,
        type: Class<*>,
        id: Long?,
        aclPermissions: Array<AclPermission>
    ): AuditableAcl {
        return updatePermissions(
            sid = PrincipalSid(principal.username),
            type = type,
            id = id,
            grantAction = true,
            aclPermissions = aclPermissions
        )
    }

    override fun grantPermissionsToSid(
        sidName: String,
        type: Class<*>,
        id: Long?,
        aclPermissions: Array<AclPermission>
    ): AuditableAcl {
        return updatePermissions(
            sid = GrantedAuthoritySid(sidName),
            type = type,
            id = id,
            grantAction = true,
            aclPermissions = aclPermissions
        )
    }

    private fun updatePermissions(sid: Sid, type: Class<*>, id: Long?, grantAction: Boolean?, aclPermissions: Array<AclPermission>): AuditableAcl {
        Assert.notEmpty(aclPermissions, "Permission must be not empty")
        requireNotNull(id)

        val acl = getOrCreate(type, id)

        val shouldRevoke = (grantAction == null)

        for (aclPermission in aclPermissions) {
            val aces = acl.entries
            var index = aces.size
            var ace: AccessControlEntry? = findAce(aces, sid, aclPermission)

            if (ace != null && (shouldRevoke || ace.isGranting != grantAction)) {
                index = aces.indexOf(ace)
                acl.deleteAce(index)
                ace = null
            }

            if (ace == null && !shouldRevoke && grantAction != null) {
                acl.insertAce(index, aclPermission, sid, grantAction)
            }
        }

        return aclService.updateAcl(acl) as AuditableAcl
    }

    private fun getOrCreate(type: Class<*>, id: Long): AuditableAcl {
        var acl = get(type, id)
        if (acl == null) {
            acl = create(type, id)
        }
        return acl
    }

    private fun get(type: Class<*>, id: Serializable): AuditableAcl? {
        return try {
            aclService.readAclById(ObjectIdentityImpl(type, id)) as AuditableAcl
        } catch (exception: NotFoundException) {
            null
        }
    }

    private fun create(type: Class<*>, id: Long): AuditableAcl {
        val acl = aclService.createAcl(ObjectIdentityImpl(type, id)) as AuditableAcl
        Assert.notNull(acl, "Acl could not be retrieved or created")
        return acl
    }

    private fun findAce(aces: List<AccessControlEntry>, sid: Sid, permission: AclPermission): AccessControlEntry? {
        for (ace in aces) {
            if (ace.sid == sid && ace.permission == permission) {
                return ace
            }
        }
        return null
    }



}
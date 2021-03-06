package com.vlbo.acl.security.services

import org.springframework.security.acls.model.ObjectIdentity
import org.springframework.security.acls.model.Sid

interface SecurityPrincipalAclService {

    fun findObjectIdentitiesWithAclForSid(sid: Sid): List<ObjectIdentity>

    fun getRegisteredAclClasses(): Collection<Class<*>>
}
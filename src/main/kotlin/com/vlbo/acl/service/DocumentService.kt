package com.vlbo.acl.service

import com.vlbo.acl.domain.acl.AclAttribute
import com.vlbo.acl.domain.model.Document
import com.vlbo.acl.domain.model.User
import com.vlbo.acl.repository.DocumentRepository
import com.vlbo.acl.security.services.AclPermission
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.GrantedAuthoritySid
import org.springframework.security.acls.domain.PrincipalSid
import org.springframework.security.acls.domain.SidRetrievalStrategyImpl
import org.springframework.security.acls.model.Permission
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class DocumentService(
    val documentRepository: DocumentRepository,
    val aclSecurityService: AclSecurityService
) {

    @Transactional
    fun create(document: Document): Document {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        document.owner = user

        val saved = documentRepository.save(document)

        aclSecurityService.grantPermissionsToUser(
            principal = user,
            type = saved.javaClass,
            id = saved.id,
            aclPermissions = arrayOf(AclPermission.READ, AclPermission.WRITE, AclPermission.ADMINISTRATION)
        )

        return saved
    }

    @PreAuthorize("hasPermission(#id, 'com.vlbo.acl.domain.model.Document', 'read')")
    fun findById(id: Long): Document {
        val findById = documentRepository.findById(id)
        return findById
            .orElseThrow { RuntimeException("Document with ID = ${id} not found") }
    }

    fun readMyAllDocumentsPage(pageNumber: Int, pageSize: Int, sort: Sort): Page<Document> {
        val sidRetrievalStrategy = MySidRetrievalStrategyImpl()
        val sids = sidRetrievalStrategy.getGrantedAuthorities(SecurityContextHolder.getContext().authentication)

        sidRetrievalStrategy.getPrincipalSid(SecurityContextHolder.getContext().authentication)?.let { sid ->
            sids.add(sid)
        }

        val pageRequest = PageRequest.of(pageNumber, pageSize, sort)

        return documentRepository.findAllByPermission(AclPermission.READ.mask, sids.toTypedArray(), pageRequest)
    }

    @PreAuthorize("hasPermission(#id, 'com.vlbo.acl.domain.model.Document', 'administration')")
    @Transactional
    fun grantPermissionsToUserGroup(id: Long, groupName: String, permissions: Array<AclPermission>) {
        aclSecurityService.grantPermissionsToSid(
            AclAttribute.GROUP.getSidForAttributeValue(groupName),
            Document::class.java,
            id,
            permissions)
    }

    private class MySidRetrievalStrategyImpl : SidRetrievalStrategyImpl() {

        fun getGrantedAuthorities(authentication: Authentication?): MutableSet<String> {
            val sids = super.getSids(authentication)
            val grantedAuthorities = mutableSetOf<String>()

            for (sid in sids) {
                if (sid is GrantedAuthoritySid) {
                    grantedAuthorities.add(sid.grantedAuthority)
                }
            }
            return grantedAuthorities
        }

        fun getPrincipalSid(authentication: Authentication?): String? {
            val sids = super.getSids(authentication)

            for (sid in sids) {
                if (sid is PrincipalSid) {
                    return sid.principal
                }
            }

            return null
        }
    }
}
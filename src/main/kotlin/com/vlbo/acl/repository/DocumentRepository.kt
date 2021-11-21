package com.vlbo.acl.repository

import com.vlbo.acl.domain.model.Document
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface DocumentRepository : PagingAndSortingRepository<Document, Long> {

    companion object {
        private const val FIND_DOCS_FOR_SID_SUB_QUERY =
            """
            FROM ACL_OBJECT_IDENTITY obj
                INNER JOIN ACL_ENTRY entry   ON entry.acl_object_identity = obj.id 
                INNER JOIN ACL_SID sid       ON entry.sid = sid.id
                INNER JOIN DOCUMENT docs ON CAST(obj.object_id_identity as bigint) = docs.id
            WHERE sid.sid IN :sid 
                AND (entry.mask = :mask) 
                AND entry.granting = true
                AND obj.object_id_class = (SELECT id FROM ACL_CLASS WHERE acl_class.class = 'com.vlbo.acl.domain.model.Document')
            """

        private const val SELECT_DOCS_FOR_SID_QUERY = "SELECT DISTINCT docs.* ${FIND_DOCS_FOR_SID_SUB_QUERY}"
        private const val COUNT_DOCS_FOR_SID_QUERY = "SELECT COUNT (DISTINCT docs.id) ${FIND_DOCS_FOR_SID_SUB_QUERY}"
    }

    @Query(value = SELECT_DOCS_FOR_SID_QUERY, countQuery = COUNT_DOCS_FOR_SID_QUERY, nativeQuery = true)
    fun findAllByPermission(@Param("mask") permissionCode: Int,
                            @Param("sid") sid: Array<String>,
                            pageable: Pageable): Page<Document>

}
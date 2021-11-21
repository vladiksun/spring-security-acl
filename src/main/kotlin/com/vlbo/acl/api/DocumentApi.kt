package com.vlbo.acl.api

import com.vlbo.acl.api.DocumentApi.Companion.PATH
import com.vlbo.acl.domain.dto.DocumentDTO
import com.vlbo.acl.domain.dto.PermissionDTO
import com.vlbo.acl.mapper.DocumentMapper
import com.vlbo.acl.service.DocumentService
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Min

@RestController
@RequestMapping(path = [PATH])
class DocumentApi(val documentMapper: DocumentMapper,
                  val documentService: DocumentService) {

    companion object {
        const val PATH = "/api/document"
        const val FIRST_PAGE_ID = 0
        const val DEFAULT_PAGE_SIZE = 20
    }

    @PostMapping
    fun create(@RequestBody @Valid document: DocumentDTO): ResponseEntity<DocumentDTO> {
        val created = documentService.create(documentMapper.asEntity(document))
        return ResponseEntity(documentMapper.asDTO(created), HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun read(@PathVariable("id") @Min(0) id: Long): ResponseEntity<DocumentDTO> {
        val findById = documentService.findById(id)
        return ResponseEntity.ok().body(documentMapper.asDTO(findById))
    }

    @GetMapping("/my")
    fun readMyAllDocuments(): ResponseEntity<List<DocumentDTO>> {
        return readMyAllDocumentsPage(FIRST_PAGE_ID, DEFAULT_PAGE_SIZE)
    }

    @PutMapping("/grantPermissionsToUserGroup/{id}")
    fun grantPermissionsToUserGroup(@PathVariable("id") @Min(0) id: Long,
                                    @RequestBody groupPermission: PermissionDTO) {

        documentService.grantPermissionsToUserGroup(id, groupPermission.name, groupPermission.permissions)
    }

    private fun readMyAllDocumentsPage(pageNumber: Int, pageSize: Int): ResponseEntity<List<DocumentDTO>> {
        val page = documentService.readMyAllDocumentsPage(pageNumber, pageSize, Sort.by("docs.name"))

        return ResponseEntity.ok().body(documentMapper.asDTOList(page.content))
    }

}
package com.vlbo.acl.api

import com.vlbo.acl.domain.dto.DocumentDTO
import com.vlbo.acl.mapper.DocumentMapper
import com.vlbo.acl.service.DocumentService
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/api/document"])
class DocumentApi(val documentMapper: DocumentMapper,
                  val documentService: DocumentService) {

    companion object {
        const val FIRST_PAGE_ID = 0
        const val DEFAULT_PAGE_SIZE = 20
    }

    @PostMapping
    fun create(document: DocumentDTO): ResponseEntity<DocumentDTO> {
        val created = documentService.create(documentMapper.asEntity(document))
        return ResponseEntity(documentMapper.asDTO(created), HttpStatus.CREATED)
    }

    @GetMapping("/my")
    fun readMyAllDocuments(): ResponseEntity<List<DocumentDTO>> {
        return readMyAllDocumentsPage(FIRST_PAGE_ID, DEFAULT_PAGE_SIZE)
    }

    private fun readMyAllDocumentsPage(pageNumber: Int, pageSize: Int): ResponseEntity<List<DocumentDTO>> {
        val page = documentService.readMyAllDocumentsPage(pageNumber, pageSize, Sort.by("docs.name"))

        return ResponseEntity.ok().body(documentMapper.asDTOList(page.content))
    }

}
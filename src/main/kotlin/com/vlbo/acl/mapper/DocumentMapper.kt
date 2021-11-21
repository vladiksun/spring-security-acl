package com.vlbo.acl.mapper

import com.vlbo.acl.domain.dto.DocumentDTO
import com.vlbo.acl.domain.model.Document
import org.mapstruct.Mapper
import org.springframework.stereotype.Component

@Component
@Mapper(componentModel = "spring")
interface DocumentMapper : GenericMapper<Document, DocumentDTO> {
}
package com.vlbo.acl.mapper

import com.vlbo.acl.domain.dto.DocumentDTO
import com.vlbo.acl.domain.model.Document
import org.mapstruct.Mapper
import org.mapstruct.MappingInheritanceStrategy
import org.springframework.stereotype.Component

@Component
@Mapper(
    componentModel = "spring",
    mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_ALL_FROM_CONFIG)
interface DocumentMapper : GenericMapper<Document, DocumentDTO> {
}
package com.vlbo.acl.domain.dto

import javax.validation.constraints.NotBlank

data class DocumentDTO(
    @NotBlank val name: String
    )

package com.vlbo.acl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.vlbo.acl.domain.dto.AuthRequestDTO
import com.vlbo.acl.domain.dto.DocumentDTO
import com.vlbo.acl.domain.dto.PermissionDTO
import org.springframework.mock.web.MockHttpServletResponse

val mapper = jacksonObjectMapper()

fun DocumentDTO.toJson(): String = mapper.writeValueAsString(this)

fun AuthRequestDTO.toJson(): String = mapper.writeValueAsString(this)

fun PermissionDTO.toJson(): String = mapper.writeValueAsString(this)

fun MockHttpServletResponse.toDocument(): DocumentDTO {
    return mapper.readValue(this.contentAsByteArray, DocumentDTO::class.java)
}

fun MockHttpServletResponse.extractToken(): String {
    val token = this.getHeaderValue("Authorization")
    return token as String
}
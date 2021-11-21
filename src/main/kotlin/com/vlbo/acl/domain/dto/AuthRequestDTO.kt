package com.vlbo.acl.domain.dto

data class AuthRequestDTO(
    val userName: String,
    val passWord: String,
    val email: String
)

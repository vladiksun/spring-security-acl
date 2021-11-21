package com.vlbo.acl.domain.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class CreateUserRequestDTO(
    @NotBlank val userName: String,
    @NotBlank val passWord: String,
    @NotBlank @Email val email: String
)

package com.vlbo.acl.mapper

import com.vlbo.acl.domain.dto.CreateUserRequestDTO
import com.vlbo.acl.domain.dto.UserViewDTO
import com.vlbo.acl.domain.model.User
import org.mapstruct.Mapper
import org.springframework.stereotype.Component

@Component
@Mapper(componentModel = "spring")
interface UserMapper : GenericMapper<User, UserViewDTO> {

    fun asEntity(createUserRequestDTO: CreateUserRequestDTO): User

}
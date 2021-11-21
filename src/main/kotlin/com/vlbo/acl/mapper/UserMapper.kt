package com.vlbo.acl.mapper

import com.vlbo.acl.domain.dto.CreateUserRequestDTO
import com.vlbo.acl.domain.dto.UserViewDTO
import com.vlbo.acl.domain.model.User
import org.mapstruct.Mapper
import org.mapstruct.MappingInheritanceStrategy
import org.springframework.stereotype.Component

@Component
@Mapper(
    componentModel = "spring",
    mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_ALL_FROM_CONFIG)
interface UserMapper : GenericMapper<User, UserViewDTO> {

    fun asEntity(createUserRequestDTO: CreateUserRequestDTO): User

}
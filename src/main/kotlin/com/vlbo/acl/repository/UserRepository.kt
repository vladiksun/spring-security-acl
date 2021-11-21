package com.vlbo.acl.repository

import com.vlbo.acl.domain.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {

    fun findByEmailIgnoreCase(email: String?): User?

}
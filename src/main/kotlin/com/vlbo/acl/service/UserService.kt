package com.vlbo.acl.service

import com.vlbo.acl.domain.model.User
import com.vlbo.acl.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.validation.ValidationException

@Service
class UserService(val userRepository: UserRepository,
                  val passwordEncoder: PasswordEncoder) {

    @Transactional
    fun create(user: User): User {
        userRepository.findByEmailIgnoreCase(user.email)?.let {
            throw ValidationException("Email in use!")
        }

        user.passWord = passwordEncoder.encode(user.passWord)

        val saved = userRepository.save(user)

        return saved
    }
}
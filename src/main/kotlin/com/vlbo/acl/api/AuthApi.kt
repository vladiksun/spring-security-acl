package com.vlbo.acl.api

import com.vlbo.acl.api.AuthApi.Companion.PATH
import com.vlbo.acl.domain.dto.AuthRequestDTO
import com.vlbo.acl.domain.dto.CreateUserRequestDTO
import com.vlbo.acl.domain.dto.UserViewDTO
import com.vlbo.acl.domain.model.User
import com.vlbo.acl.mapper.UserMapper
import com.vlbo.acl.service.UserService
import com.vlbo.acl.security.services.JwtService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping(path = [PATH])
class AuthApi(val authenticationManager: AuthenticationManager,
              val jwtService: JwtService,
              val userMapper: UserMapper, val userService: UserService
) {

    companion object {
        const val PATH = "/api/public"
    }

    @PostMapping("login")
    fun login(@RequestBody @Valid request: AuthRequestDTO): ResponseEntity<UserViewDTO> {
        return try {
            val authenticate = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(request.email, request.passWord))

            val user: User = authenticate.principal as User

            ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtService.createJwt(user))
                .body(userMapper.asDTO(user))
        } catch (ex: BadCredentialsException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @PostMapping("register")
    fun register(@RequestBody request: @Valid CreateUserRequestDTO): ResponseEntity<UserViewDTO> {
        val created = userService.create(userMapper.asEntity(request))
        return ResponseEntity(userMapper.asDTO(created), HttpStatus.CREATED)
    }

}
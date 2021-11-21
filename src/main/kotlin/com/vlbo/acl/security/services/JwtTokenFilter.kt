package com.vlbo.acl.security.services

import com.vlbo.acl.domain.model.User
import com.vlbo.acl.repository.UserRepository
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtTokenFilter(private val userRepository: UserRepository,
                     private val jwtService: JwtService
): OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Get authorization header and validate

        // Get authorization header and validate
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (header.isNullOrEmpty() || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        // Get jwt token and validate

        // Get jwt token and validate
        val token = header.split(" ".toRegex()).toTypedArray()[1].trim { it <= ' ' }
        if (!jwtService.validate(token)) {
            filterChain.doFilter(request, response)
            return
        }

        // Get user identity and set it on the spring security context
        val userDetails: User? = userRepository.findByEmailIgnoreCase(jwtService.getUsername(token))
        val authorities = jwtService.getAuthorities(token)
        userDetails?.setAuthorities(authorities)

        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, authorities).apply {
            details = WebAuthenticationDetailsSource().buildDetails(request)
        }

        authentication.authorities
        SecurityContextHolder.getContext().authentication = authentication

        filterChain.doFilter(request, response)
    }


}
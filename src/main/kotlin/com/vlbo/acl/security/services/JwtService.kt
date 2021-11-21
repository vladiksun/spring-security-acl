package com.vlbo.acl.security.services

import com.vlbo.acl.domain.model.User
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.crypto.SecretKey

@Component
class JwtService {

    companion object {
        const val USER_AUTHORITIES = "USER_AUTHORITIES"
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${jwt.signing.key}")
    lateinit var jwtSigningKey: String
    lateinit var secretKey: SecretKey
    lateinit var jwtParser: JwtParser

    @PostConstruct
    private fun init() {
        secretKey = Keys.hmacShaKeyFor(jwtSigningKey.toByteArray())
        jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build()
    }

    fun createJwt(user: User): String {
        val authorities = user.grantedAuthorities.map { it.authority }

        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setSubject("${user.id},${user.email}")
            .claim(USER_AUTHORITIES, authorities)
            .signWith(secretKey)
            .compact()
    }

    fun getUserId(token: String): String {
        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body

        return claims.subject.split(",".toRegex()).toTypedArray()[0]
    }

    fun getUsername(token: String): String {
        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body

        return claims.subject.split(",".toRegex()).toTypedArray()[1]
    }

    fun getAuthorities(token: String): MutableList<GrantedAuthority> {
        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body

        val authorities = mutableListOf<GrantedAuthority>()

        claims[USER_AUTHORITIES]?.let { authoritiesCollection ->
            if (authoritiesCollection is Collection<*>) {
                authoritiesCollection.mapTo(authorities) { authority ->
                    SimpleGrantedAuthority(authority.toString())
                }
            }
        }

        return authorities
    }

    fun validate(token: String?): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
            return true
        } catch (ex: SignatureException) {
            logger.error("Invalid JWT signature - {}", ex.message)
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token - {}", ex.message)
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token - {}", ex.message)
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token - {}", ex.message)
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty - {}", ex.message)
        }
        return false
    }
}
package com.vlbo.acl.config

import com.vlbo.acl.repository.UserRepository
import com.vlbo.acl.services.JwtTokenFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@EnableGlobalMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true)
class WebSecurityConfig(
    private val userRepository: UserRepository,
    private val jwtTokenFilter: JwtTokenFilter
): WebSecurityConfigurerAdapter(), WebMvcConfigurer {

    @Value("\${cors.allowed.origins}")
    lateinit var corsAllowedOrigins: String

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(object : UserDetailsService {
            override fun loadUserByUsername(username: String): UserDetails? {
                return userRepository.findByEmailIgnoreCase(username)
            }
        })
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder? {
        return BCryptPasswordEncoder()
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        // Set session management to stateless
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            // Enable CORS and disable CSRF
            .and().cors().and().csrf().disable()
            // Set permissions on endpoints
            .authorizeRequests()
            // Our public endpoints
            .mvcMatchers("/api/public/**").permitAll()
            // Our private endpoints
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)


    }

    @Bean
    @Throws(java.lang.Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(corsAllowedOrigins)
            .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
            .allowCredentials(true)
            .allowedOriginPatterns("*")
            .allowedHeaders("*")
            .maxAge(3600)
    }
}
package com.vlbo.acl.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig: WebMvcConfigurer {

    @Value("\${cors.allowed.origins}")
    lateinit var corsAllowedOrigins: String

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
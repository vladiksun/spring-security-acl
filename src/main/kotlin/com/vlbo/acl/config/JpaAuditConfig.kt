package com.vlbo.acl.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*


@Configuration
@EnableJpaAuditing
class JpaAuditConfig {

    @Bean
    fun auditorProvider(): AuditorAware<String> {

        return AuditorAware<String> {
            Optional.ofNullable(SecurityContextHolder.getContext().authentication.name)
        }
    }

}
package com.vlbo.acl

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = ["com.vlbo.acl.repository"])
class SpringSecurityAclApplication

fun main(args: Array<String>) {
    runApplication<SpringSecurityAclApplication>(*args)
}

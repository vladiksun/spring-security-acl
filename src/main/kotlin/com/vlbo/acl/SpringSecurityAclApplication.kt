package com.vlbo.acl

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringSecurityAclApplication

fun main(args: Array<String>) {
	runApplication<SpringSecurityAclApplication>(*args)
}

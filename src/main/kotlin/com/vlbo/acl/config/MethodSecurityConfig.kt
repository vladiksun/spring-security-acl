package com.vlbo.acl.config

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration

@Configuration
class MethodSecurityConfig(
    private val context: ApplicationContext,
    private val permissionEvaluator: PermissionEvaluator
): GlobalMethodSecurityConfiguration() {

    override fun createExpressionHandler(): MethodSecurityExpressionHandler? {
        val expressionHandler = DefaultMethodSecurityExpressionHandler()
        // You can also implement a custom one as explained here
        // https://www.baeldung.com/spring-security-create-new-custom-security-expression
        expressionHandler.setPermissionEvaluator(permissionEvaluator)
        expressionHandler.setApplicationContext(context)
        return expressionHandler
    }

}
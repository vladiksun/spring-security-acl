package com.vlbo.acl.config

import com.vlbo.acl.security.services.*
import com.zaxxer.hikari.HikariDataSource
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.acls.AclPermissionEvaluator
import org.springframework.security.acls.domain.*
import org.springframework.security.acls.jdbc.BasicLookupStrategy
import org.springframework.security.acls.jdbc.LookupStrategy
import org.springframework.security.acls.model.AclCache
import org.springframework.security.acls.model.MutableAclService
import org.springframework.security.acls.model.PermissionGrantingStrategy
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Configuration
class AclSecurityConfiguration(private val cacheManager: CacheManager) {

    @Bean
    fun aclPermissionFactory(): AclPermissionFactory {
        return DefaultAclPermissionFactory()
    }

    @Bean
    fun permissionEvaluator(aclService: MutableAclService): PermissionEvaluator {
        val evaluator = AclPermissionEvaluator(aclService)
        evaluator.setPermissionFactory(aclPermissionFactory())
        return evaluator
    }

    @Bean
    fun aclService(dataSource: HikariDataSource, lookupStrategy: LookupStrategy ): MutableAclService {
        val aclService = CustomJdbcAclService(dataSource, lookupStrategy, aclCache(), cacheInstance())

        aclService.setAclClassIdSupported(true)

        if (dataSource.driverClassName == "org.postgresql.Driver") {
            // because of PostgreSQL as documented here:
            // https://docs.spring.io/spring-security/reference/servlet/appendix/database-schema.html#_postgresql
            aclService.setClassIdentityQuery("select currval(pg_get_serial_sequence('acl_class', 'id'))")
            aclService.setSidIdentityQuery("select currval(pg_get_serial_sequence('acl_sid', 'id'))")
        }

        return aclService
    }

    @Bean
    fun aclCache(): AclCache {
        return SpringCacheBasedAclCache(
            cacheInstance(),
            permissionGrantingStrategy(),
            aclAuthorizationStrategy())
    }

    @Bean
    fun lookupStrategy(dataSource: HikariDataSource): LookupStrategy {
        val lookupStrategy = BasicLookupStrategy(
            dataSource,
            aclCache(),
            aclAuthorizationStrategy(),
            permissionGrantingStrategy()
        )
        lookupStrategy.setPermissionFactory(aclPermissionFactory())
        lookupStrategy.setAclClassIdSupported(true)
        return lookupStrategy
    }

    @Bean
    fun permissionGrantingStrategy(): PermissionGrantingStrategy? {
        return DefaultPermissionGrantingStrategy(
            AclAuditLogger()
        )
    }

    @Bean
    fun aclAuthorizationStrategy(): AclAuthorizationStrategy {
        val strategy = AclAuthorizationStrategyImpl(
            SimpleGrantedAuthority(AclAuthorities.ROLE_ACL_ADMIN)
        )
        strategy.setSidRetrievalStrategy(SidRetrievalStrategyImpl())
        return strategy
    }

    private fun cacheInstance(): Cache? {
        return cacheManager.getCache(AclConstants.CACHE_NAME)
    }

}
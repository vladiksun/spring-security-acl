package com.vlbo.acl.config

import com.vlbo.acl.services.AclConstants
import org.springframework.cache.CacheManager
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EnableCachingConfiguration {

    @Bean
    fun cacheManager(): CacheManager {
        return SimpleCacheManager().apply {
            setCaches(listOf(
                aclCache()
            ))
        }
    }

    private fun aclCache(): ConcurrentMapCache {
        return ConcurrentMapCache(AclConstants.CACHE_NAME)
    }
}
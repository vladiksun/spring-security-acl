package com.vlbo.acl.security.services

import org.slf4j.LoggerFactory
import org.springframework.security.acls.domain.AuditLogger
import org.springframework.security.acls.model.AccessControlEntry
import org.springframework.security.acls.model.AuditableAccessControlEntry
import org.springframework.util.Assert

class AclAuditLogger : AuditLogger {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun logIfNeeded(isGranted: Boolean, ace: AccessControlEntry) {
        Assert.notNull(ace, "AccessControlEntry required")
        if (ace is AuditableAccessControlEntry) {
            val auditableAce = ace

            // log only in case ACE configures auditSuccess = true
            if (isGranted && auditableAce.isAuditSuccess) {
                logger.info("GRANTED due to ACE: $ace")
            }
            // log only in case ACE configures auditFailure = true
            if (!isGranted && auditableAce.isAuditFailure) {
                logger.warn("DENIED due to ACE: $ace")
            }
        }
    }

    fun logGrantPermission(ace: AccessControlEntry) {
        Assert.notNull(ace, "AccessControlEntry required")
        logger.info("CREATED ACE: $ace")
    }

    fun logRemovePermission(ace: AccessControlEntry) {
        Assert.notNull(ace, "AccessControlEntry required")
        logger.info("REMOVED ACE: $ace")
    }
}
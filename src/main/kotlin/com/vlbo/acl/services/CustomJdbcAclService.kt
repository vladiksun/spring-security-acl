package com.vlbo.acl.services

import org.apache.commons.lang3.ClassUtils
import org.springframework.cache.Cache
import org.springframework.security.acls.domain.ObjectIdentityImpl
import org.springframework.security.acls.jdbc.JdbcMutableAclService
import org.springframework.security.acls.jdbc.LookupStrategy
import org.springframework.security.acls.model.AclCache
import org.springframework.security.acls.model.ObjectIdentity
import org.springframework.security.acls.model.Sid
import org.springframework.transaction.annotation.Transactional
import javax.sql.DataSource

open class CustomJdbcAclService(
    private val dataSource: DataSource,
    private val lookupStrategy: LookupStrategy,
    private val aclCache: AclCache?,
    private val cache: Cache?
) : JdbcMutableAclService(dataSource, lookupStrategy, aclCache), SecurityPrincipalAclService {

    companion object {
        private const val SELECT_OBJECT_IDENTITIES_FOR_SID =
            """
                SELECT DISTINCT obj.object_id_identity as obj_id, class.class as class 
                FROM acl_entry entry 
                INNER JOIN acl_object_identity obj ON obj.id = entry.acl_object_identity
                INNER JOIN acl_class class ON obj.object_id_class = class.id
                WHERE entry.sid = ?
            """

        private const val SELECT_ALL_CLASSES = "SELECT class FROM acl_class"
    }

    override fun createOrRetrieveSidPrimaryKey(sid: Sid, allowCreate: Boolean): Long {
        val sidId: Long
        val cachedSid = cache?.get(sid, Long::class.java)

        if (cachedSid != null) {
            return cachedSid
        } else {
            sidId = super.createOrRetrieveSidPrimaryKey(sid, allowCreate)
            cache?.put(sid, sidId)
        }
        return sidId
    }

    @Transactional(readOnly = true)
    override fun createOrRetrieveClassPrimaryKey(type: String, allowCreate: Boolean, idType: Class<*>?): Long {
        val classId: Long
        val cachedClassId = cache?.get(Long, Long::class.java)
        if (cachedClassId != null) {
            return cachedClassId
        } else {
            classId = super.createOrRetrieveClassPrimaryKey(type, allowCreate, idType)
            cache?.put(type, classId)
        }
        return classId
    }

    @Transactional(readOnly = true)
    override fun findObjectIdentitiesWithAclForSid(sid: Sid): List<ObjectIdentity> {
        val ownerId = createOrRetrieveSidPrimaryKey(sid, false) ?: return emptyList()
        val args = arrayOf<Any>(ownerId)
        return jdbcOperations.query(
            SELECT_OBJECT_IDENTITIES_FOR_SID,
            { rs, rowNum ->
                val javaType: String = rs.getString("class")
                val identifier: Long = rs.getLong("obj_id")
                ObjectIdentityImpl(
                    javaType,
                    identifier
                )
            },
            args
        )
    }

    override fun getRegisteredAclClasses(): Collection<Class<*>> {
        jdbcOperations
        val classNames: List<String> = jdbcOperations.query(
            SELECT_ALL_CLASSES
        ){ rs, rowNum -> rs.getString("class") }

        val classes: MutableSet<Class<*>> = HashSet()

        for (className in classNames) {
            try {
                val clazz: Class<*> = ClassUtils.getClass(className)
                classes.add(clazz)
            } catch (ignore: ClassNotFoundException) {
            }
        }

        return classes
    }

}
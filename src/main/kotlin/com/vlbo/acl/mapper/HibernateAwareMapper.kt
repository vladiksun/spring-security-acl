package com.vlbo.acl.mapper

import org.hibernate.collection.spi.PersistentCollection
import org.mapstruct.BeforeMapping
import org.mapstruct.Mapper
import org.mapstruct.MappingInheritanceStrategy
import org.mapstruct.TargetType
import org.springframework.stereotype.Component
import java.util.*


@Component
@Mapper(
    componentModel = "spring",
    mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_ALL_FROM_CONFIG)
interface HibernateAwareMapper {

    @BeforeMapping
    fun <T> fixLazyLoadingSet(c: Collection<*>?, @TargetType targetType: Class<*>?): Set<T>? {
        return if (!Util.wasInitialized(c)) {
            Collections.emptySet()
        } else null
    }

    @BeforeMapping
    fun <T> fixLazyLoadingList(c: Collection<*>?, @TargetType targetType: Class<*>?): List<T>? {
        return if (!Util.wasInitialized(c)) {
            Collections.emptyList()
        } else null
    }

    object Util {
        fun wasInitialized(c: Any?): Boolean {
            if (c !is PersistentCollection) {
                return true
            }
            return c.wasInitialized()
        }
    }
}
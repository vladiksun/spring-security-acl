package com.vlbo.acl.services

import org.springframework.security.acls.domain.AbstractPermission
import org.springframework.security.acls.domain.CumulativePermission

class AclPermission(mask: Int, code: Char): AbstractPermission(mask, code) {

    companion object {
        val READ = AclPermission(1 shl 0, 'R') // 1
        val WRITE = AclPermission(1 shl 1, 'W') // 2
        val CREATE = AclPermission(1 shl 2, 'C') // 4
        val DELETE = AclPermission(1 shl 3, 'D') // 8
        val ADMINISTRATION = AclPermission(1 shl 4, 'A') // 16
    }

    /**
     * Create a new dynamic permission using the specified bit position.
     * Because all bit positions below 10 are considered privileged, this method will throw
     * an `IllegalArgumentException` if you attempt to create one. The same will happen
     * if you attempt to go outside the integer range (max shift = 31).
     *
     *
     * Specifying a position of `11` will result in a permission being created with
     * a mask of `1 << 11`.
     *
     * @param position bit position that should be used for this permission
     * @return permission
     */
    fun create(position: Int): AclPermission {
        return create(position, '*')
    }

    /**
     * Create a new dynamic permission using the specified bit position.
     * Because all bit positions below 10 are considered privileged, this method will throw
     * an `IllegalArgumentException` if you attempt to create one. The same will happen
     * if you attempt to go outside the integer range (max shift = 31).
     *
     *
     * Specifying a position of `11` will result in a permission being created with
     * a mask of `1 << 11`.
     *
     * @param position bit position that should be used for this permission
     * @param code     to use on this bit position when building the pattern
     * @return permission
     */
    fun create(position: Int, code: Char): AclPermission {
        require(position >= 10) { "Permission bit positions < 10 are considered reseved" }
        require(position < 32) { "Bit position must be < 32" }
        return AclPermission(1 shl position, code)
    }

    /**
     * Builds a new cumulative permission, that is a combination of several others.
     *
     * @param permissions to combine
     * @return cumulative permission
     */
    fun combine(vararg permissions: AclPermission?): CumulativePermission {
        val permission = CumulativePermission()

        permissions.forEach {
            permission.set(it)
        }

        return permission
    }

}
package com.vlbo.acl.security.services

import org.springframework.security.acls.domain.PermissionFactory
import org.springframework.security.acls.model.Permission

/**
 * Extension of the {@link PermissionFactory} from Spring security ACL.
 * Allows for manually registering additional permissions using {@link #registerPermission(Permission, String)},
 * as well as retrieving the name for a registered permission using {@link #getNameForPermission(Permission)}.
 * <p/>
 * Any custom permission registered can also be used in expression based access control, eg:
 * <pre>{@code
 * @PreAuthorize("hasPermission(#object, 'my-custom-permission')")
 * void customPermission( Object object ) {
 *    ...
 * }
 * }</pre>
 *
 */
interface AclPermissionFactory: PermissionFactory {


    /**
     * Retrieve the registered name for a permission.
     * Will throw an {@code IllegalArgumentException} if the permission is not registered.
     * <p/>
     * This method cannot be used for {@link org.springframework.security.acls.domain.CumulativePermission} instances
     * unless they are also registered.
     *
     * @param permission to get the name for
     * @return registered name
     */
    fun getNameForPermission(permission: Permission): String

    /**
     * Register an additional permission to the permission factory.
     *
     * @param permission     to register
     * @param permissionName associated with that permission
     */
    fun registerPermission(permission: Permission, permissionName: String)
}
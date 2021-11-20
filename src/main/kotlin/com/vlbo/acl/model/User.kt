package com.vlbo.acl.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(name = "USER",
    uniqueConstraints = [
        UniqueConstraint(name = "IDX_EMAIL_UNIQUE", columnNames = ["EMAIL"])
    ])
class User(): LongIdEntity(), UserDetails {

    @Column(name = "USER_NAME", nullable = false)
    var userName: String? = null

    @Column(name = "PASSWORD", nullable = false)
    var passWord: String? = null

    @Column(name = "EMAIL", nullable = false)
    var email: String? = null

    private val enabled = true

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        TODO("Not yet implemented")
    }

    override fun getPassword(): String? {
        return this.passWord
    }

    override fun getUsername(): String? {
        return this.userName
    }

    override fun isAccountNonExpired(): Boolean {
        return this.enabled
    }

    override fun isAccountNonLocked(): Boolean {
        return this.enabled
    }

    override fun isCredentialsNonExpired(): Boolean {
        return this.enabled
    }

    override fun isEnabled(): Boolean {
        return this.enabled
    }
}
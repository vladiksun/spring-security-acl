package com.vlbo.acl.domain.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class Document(): LongIdEntity() {

    @Column(name = "NAME", nullable = false)
    var name: String? = null

    @ManyToOne
    @JoinColumn(name = "OWNER_ID")
    var owner: User? = null

}
package com.vlbo.acl.domain.dto

import javax.validation.constraints.NotBlank

class DocumentDTO(@NotBlank val name: String) {

    var id: Long? = null

}

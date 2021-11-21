package com.vlbo.acl

import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.web.servlet.request.RequestPostProcessor

class BearerTokenRequestPostProcessor(private val token: String) : RequestPostProcessor {

    override fun postProcessRequest(request: MockHttpServletRequest): MockHttpServletRequest {
        request.addHeader("Authorization", "Bearer " + token)
        return request
    }
}

fun bearerToken(token: String): BearerTokenRequestPostProcessor {
    return BearerTokenRequestPostProcessor(token)
}
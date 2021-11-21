package com.vlbo.acl

import com.vlbo.acl.api.AuthApi
import com.vlbo.acl.api.DocumentApi
import com.vlbo.acl.domain.acl.AclAttribute
import com.vlbo.acl.domain.dto.AuthRequestDTO
import com.vlbo.acl.domain.dto.DocumentDTO
import com.vlbo.acl.domain.dto.PermissionDTO
import com.vlbo.acl.domain.model.User
import com.vlbo.acl.repository.UserRepository
import com.vlbo.acl.security.services.JwtService
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.context.support.TestPropertySourceUtils
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@SqlGroup(value = [
    Sql(scripts = ["classpath:sql/clean.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
])
@ContextConfiguration(initializers = [AclTest.DockerPostgreDataSourceInitializer::class])
@ActiveProfiles("test")
class AclTest(@Autowired val mockMvc: MockMvc,
              @Autowired val jwtService: JwtService,
              @Autowired val passwordEncoder: PasswordEncoder,
              @Autowired val userRepository: UserRepository) {


    @Test
    fun `Anyone can create and read its document`() {
        val (token, id) = `Create user`("user1@user1").let {
            `Login user and retrieve token`(it).let { token ->
                val id = `Create document and get ID`(token, "New_document")
                Pair(token, id)
            }
        }

        mockMvc.perform(buildGetRequest(id!!.toString())
                .with(bearerToken(token)))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", `is`("New_document")))
    }

    @Test
    fun `Anyone cannot read others document`() {
        val (token, id) = `Create user`("user1@user1").let {
            `Login user and retrieve token`(it).let { token ->
                val id = `Create document and get ID`(token, "New_document")
                Pair(token, id)
            }
        }

        val tokenAlien = `Create user`("user2@user2").let {
            `Login user and retrieve token`(it)
        }

        mockMvc.perform(buildGetRequest(id!!.toString())
            .with(bearerToken(tokenAlien)))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `Anyone can read all its documents`() {
        val token = `Create user`("user1@user1").let {
            `Login user and retrieve token`(it).let { token ->
               `Create documents`(token, "Doc", 10)
                token
            }
        }

        mockMvc.perform(buildGetRequest("/my")
            .with(bearerToken(token)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize<Any>(10)))
    }

    @Test
    fun `Anyone cannot read others documents`() {
        `Create user`("user1@user1").let {
            `Login user and retrieve token`(it).let { token ->
                `Create documents`(token, "Doc", 10)
            }
        }

        val tokenAlien = `Create user`("user2@user2").let {
            `Login user and retrieve token`(it)
        }

        mockMvc.perform(buildGetRequest("/my")
            .with(bearerToken(tokenAlien)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize<Any>(0)))
    }

    @Test
    fun `Authorized group member can read all group documents`() {
        val (token, id) = `Create user`("user1@user1").let {
            `Login user and retrieve token`(it).let { token: String ->
                val id = `Create document and get ID`(token, "New_document")
                Pair(token, id)
            }
        }

        val permissionCodes = arrayOf('R')
        val jwt = `Grant permission to group and get authority attribute`(id.toString(), token, permissionCodes).let { authority ->
            `Create user`("user2@user2").let { user ->
                user.grantedAuthorities = mutableListOf(SimpleGrantedAuthority(authority))
                val jwt = jwtService.createJwt(user)
                jwt
            }
        }

        mockMvc.perform(buildGetRequest("/my")
            .with(bearerToken(jwt)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize<Any>(1)))
    }

    private fun `Grant permission to group and get authority attribute`(id: String, token: String, permissionCodes: Array<Char>): String {
        mockMvc.perform(buildGrantPermissionToUserGroupRequest(id, createPermissions(GROUP, permissionCodes))
                .with(bearerToken(token)))
            .andExpect(status().isOk)

        val authorityAttribute = AclAttribute.GROUP.getSidForAttributeValue(GROUP)
        return authorityAttribute
    }

    private fun createPermissions(name: String, permissionCodes: Array<Char>): PermissionDTO {
        return PermissionDTO().apply {
            this.name = name
            this.permissionCodes = permissionCodes
        }
    }


    private fun `Create user`(emailUnique: String): User {
        val saved = userRepository.save(
            User().apply {
                userName = emailUnique
                email = emailUnique
                passWord = passwordEncoder.encode(emailUnique)
            })

        return saved
    }

    private fun `Login user and retrieve token`(user: User): String {
        val authRequestDTO = AuthRequestDTO(email = user.email, passWord = user.email)

        val token = mockMvc.perform(
            post("${AuthApi.PATH}/login")
                .content(authRequestDTO.toJson())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andReturn()
            .response.extractToken()

        return token
    }

    private fun `Create document and get ID`(jwtToken: String, name: String): Long? {
        return postDocumentAndGetId(jwtToken, DocumentDTO(name))
    }

    private fun `Create documents`(jwtToken: String, nameMask: String, number: Int) {
        (1..number).forEach {
            postDocumentAndGetId(jwtToken, DocumentDTO("${nameMask}_${it}"))
        }
    }

    private fun postDocumentAndGetId(jwtToken: String, document: DocumentDTO): Long? {
        val response = mockMvc.perform(
            buildPostRequest(document)
                .with(bearerToken(jwtToken)))
                .andExpect(status().isCreated)
                .andReturn()
            .response.toDocument()

        return response.id
    }

    private fun buildGrantPermissionToUserGroupRequest(id: String, groupPermission: PermissionDTO): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.put("${DocumentApi.PATH}/grantPermissionsToUserGroup/${id}")
            .content(groupPermission.toJson())
            .contentType(MediaType.APPLICATION_JSON)
    }

    private fun buildPostRequest(document: DocumentDTO): MockHttpServletRequestBuilder {
        return post(DocumentApi.PATH)
                .content(document.toJson())
                .contentType(MediaType.APPLICATION_JSON)
    }

    private fun buildGetRequest(path: String): MockHttpServletRequestBuilder {
        return get("${DocumentApi.PATH}/${path}")
    }

    companion object {
        private const val GROUP = "GROUP_1"

        // will be reused for each test method in the class
        @Container
        private val postgresqlContainer: PostgreSQLContainer<*> =
            PostgreSQLContainer(DockerImageName.parse("postgres").withTag("13.1"))
                .withDatabaseName("postgres")
                .withUsername("postgres")
                .withPassword("postgres")
    }

    // will be started before and stopped after each test method
//    @Container
//    private val postgresqlContainer: PostgreSQLContainer<*> =
//        PostgreSQLContainer(DockerImageName.parse("postgres").withTag("13.1"))
//            .withDatabaseName("postgres")
//            .withUsername("postgres")
//            .withPassword("postgres")

    class DockerPostgreDataSourceInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            val jdbcUrl = postgresqlContainer.jdbcUrl

            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext,
                "spring.datasource.url=$jdbcUrl",
                "spring.datasource.username=" + postgresqlContainer.username,
                "spring.datasource.password=" + postgresqlContainer.password
            )
        }
    }

}
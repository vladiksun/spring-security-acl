package com.vlbo.acl

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.context.support.TestPropertySourceUtils
import org.springframework.test.web.servlet.MockMvc
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
class AclTest(@Autowired val mockMvc: MockMvc) {

    companion object {
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

    @Test
    fun `test`() {
        val test = ""
    }

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
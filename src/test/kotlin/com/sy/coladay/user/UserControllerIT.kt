package com.sy.coladay.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.sy.coladay.common.ITConfig
import lombok.SneakyThrows
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.hateoas.MediaTypes
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.util.*

@SpringBootTest
@ITConfig
internal class UserControllerIT {
    @Autowired
    var mockMvc: MockMvc? = null

    @Autowired
    var objectMapper: ObjectMapper? = null

    @Autowired
    var userRepository: UserRepository? = null

    var cokeUser: User? = null

    @BeforeEach
    fun setUp(webApplicationContext: WebApplicationContext?,
              restDocumentation: RestDocumentationContextProvider?) {
        cokeUser = userRepository!!.findByName("user1").get()
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply<DefaultMockMvcBuilder>(
                MockMvcRestDocumentation.documentationConfiguration(
                    restDocumentation
                )
            )
            .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
            .build()
    }

    @Test
    @SneakyThrows
    fun `attempt to create a user return 405`() {

        mockMvc!!.perform(post("/users")
                .with(httpBasic(cokeUser!!.name, cokeUser!!.password))
                .content(objectMapper!!.writeValueAsString(Collections.EMPTY_MAP))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed)
    }

    @Test
    @SneakyThrows
    fun `attempt to delete a user return405`() {

        mockMvc!!.perform(delete("/users/{id}", 1)
                .with(httpBasic(cokeUser!!.name, cokeUser!!.password))
                .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed)
    }

    @SneakyThrows
    @Test
    fun `get all users return 200`() {

        mockMvc!!.perform(get("/users")
            .with(httpBasic(cokeUser!!.name, cokeUser!!.password))
            .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$._embedded.users[0].name", `is`("user1")))
            .andExpect(jsonPath("$._embedded.users[0].company", `is`("COKE")))
            .andExpect(jsonPath("$._embedded.users[0]._links.length()", `is`(2)))
            .andExpect(jsonPath("$._embedded.users[0]._links.self", notNullValue()))
            .andExpect(jsonPath("$._embedded.users[0]._links.user", notNullValue()))
            .andExpect(jsonPath("$._embedded.users[1].name", `is`("user2")))
            .andExpect(jsonPath("$._embedded.users[1].company", `is`("PEPSI")))
            .andExpect(jsonPath("$._embedded.users[1]._links.length()", `is`(2)))
            .andExpect(jsonPath("$._embedded.users[1]._links.self", notNullValue()))
            .andExpect(jsonPath("$._embedded.users[1]._links.user", notNullValue()))
            .andExpect(jsonPath("$._links.length()", `is`(2)))
            .andExpect(jsonPath("$._links.profile", notNullValue()))
            .andDo(MockMvcRestDocumentation.document("list-all-users"))
    }

    @SneakyThrows
    @Test
    fun `get all users return 401 when credentials are bad`() {

        mockMvc!!.perform(get("/users")
            .with(httpBasic("unknownUser", "aPassword"))
            .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect(MockMvcResultMatchers.status().reason("Unauthorized"))
    }
}
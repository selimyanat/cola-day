package com.sy.coladay.room

import com.fasterxml.jackson.databind.ObjectMapper
import com.sy.coladay.common.ITConfig
import com.sy.coladay.user.User
import com.sy.coladay.user.UserRepository
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
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.util.*

/**
 * ITest for auto generated room controller class.
 */
@SpringBootTest
@ITConfig
internal class RoomControllerIT {
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
            .apply<DefaultMockMvcBuilder>(documentationConfiguration(restDocumentation))
            .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
            .build()
    }

    @Test
    @SneakyThrows
    fun `attempt to create a room return 405`() {

        mockMvc!!.perform(post("/rooms")
            .with(httpBasic(cokeUser!!.name, cokeUser!!.password))
            .content(objectMapper!!.writeValueAsString(Collections.EMPTY_MAP))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isMethodNotAllowed)
    }

    @Test
    @SneakyThrows
    fun `attempt to delete a room return 405`() {

        mockMvc!!.perform(delete("/rooms/{id}", 1)
            .with(httpBasic(cokeUser!!.name, cokeUser!!.password))
            .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isMethodNotAllowed)
    }

    @SneakyThrows
    @Test
    fun `get rooms return 200`() {

        mockMvc!!.perform(get("/rooms?size=1")
            .with(httpBasic(cokeUser!!.name, cokeUser!!.password))
            .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._embedded.rooms[0].name", `is`("COKE_R01")))
            .andExpect(jsonPath("$._embedded.rooms[0].owner", `is`("COKE")))
            .andExpect(jsonPath("$._embedded.rooms[0]._links.length()", `is`(3)))
            .andExpect(jsonPath("$._embedded.rooms[0]._links.self", notNullValue()))
            .andExpect(jsonPath("$._embedded.rooms[0]._links.room", notNullValue()))
            .andExpect(jsonPath("$._embedded.rooms[0]._links.reservations", notNullValue()))
            .andExpect(jsonPath("$._links.length()", `is`(5)))
            .andExpect(jsonPath("$._links.self", notNullValue()))
            .andExpect(jsonPath("$._links.first", notNullValue()))
            .andExpect(jsonPath("$._links.next", notNullValue()))
            .andExpect(jsonPath("$._links.last", notNullValue()))
            .andExpect(jsonPath("$._links.profile", notNullValue()))
            .andExpect(jsonPath("$.page", notNullValue()))
            .andExpect(jsonPath("$.page.size", `is`(1)))
            .andExpect(jsonPath("$.page.totalElements", `is`(20)))
            .andExpect(jsonPath("$.page.totalPages", `is`(20)))
            .andExpect(jsonPath("$.page.number", `is`(0)))
            .andDo(MockMvcRestDocumentation.document("list-all-rooms"))
        }

    @SneakyThrows
    @Test
    fun `get all rooms return 401 when credentials are bad`() {

        mockMvc!!.perform(get("/rooms")
            .with(httpBasic("unknownUser", "aPassword"))
            .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isUnauthorized)
            .andExpect(status().reason("Unauthorized"))
    }
}
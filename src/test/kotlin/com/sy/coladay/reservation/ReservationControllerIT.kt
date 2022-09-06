package com.sy.coladay.reservation

import com.fasterxml.jackson.databind.ObjectMapper
import com.sy.coladay.common.ITConfig
import com.sy.coladay.user.User
import com.sy.coladay.user.UserRepository
import lombok.SneakyThrows
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.hateoas.MediaTypes
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.web.context.WebApplicationContext

/**
 * ITest for auto generated reservation controller class.
 */
@SpringBootTest(properties = ["coke.quota=2", "pepsi.quota=1"])
@ITConfig
@Sql(value = ["/reservations-fixtures.sql"])
internal class ReservationControllerIT {
    @Autowired
    var mockMvc: MockMvc? = null

    @Autowired
    var objectMapper: ObjectMapper? = null

    @Autowired
    var userRepository: UserRepository? = null

    @Autowired
    var reservationMetrics: ReservationMetrics? = null

    var cokeUser: User? = null

    var pepsiUser: User? = null

    @BeforeEach
    fun setUp(webApplicationContext: WebApplicationContext?,
              restDocumentation: RestDocumentationContextProvider?) {
        cokeUser = userRepository!!.findByName("user1").get()
        pepsiUser = userRepository!!.findByName("user2").get()
        mockMvc = webAppContextSetup(webApplicationContext)
            .apply<DefaultMockMvcBuilder>(documentationConfiguration(restDocumentation))
            .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
            .build()
    }

    @Test
    @SneakyThrows
    fun `post a reservation return_201`() {
        assertThat(reservationMetrics!!.cokeNumberOfReservations.count()).isEqualTo(0.0)
        assertThat(reservationMetrics!!.pepsiNumberOfReservations.count()).isEqualTo(0.0)

        val reservation = mapOf(
            "timeSlot" to TimeSlots.NINE_AM_TO_TEN_AM.name,
            "room" to  "/rooms/1")
        mockMvc!!.perform(post("/reservations")
            .with(httpBasic(cokeUser!!.name, cokeUser!!.password))
            .content(objectMapper!!.writeValueAsString(reservation))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.header().exists("location"))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.timeSlot", `is`("NINE_AM_TO_TEN_AM")))
            .andExpect(jsonPath("$._links.length()", `is`(4)))
            .andExpect(jsonPath("$._links.self", notNullValue()))
            .andExpect(jsonPath("$._links.reservation", notNullValue()))
            .andExpect(jsonPath("$._links.organizer", notNullValue()))
            .andExpect(jsonPath("$._links.room", notNullValue()))
            .andDo(document("create-a-reservation"))
        assertThat(reservationMetrics!!.cokeNumberOfReservations.count()).isEqualTo(1.0)
        assertThat(reservationMetrics!!.pepsiNumberOfReservations.count()).isEqualTo(0.0)
    }

    @Test
    @SneakyThrows
    fun `post a reservation without credentials return_401`() {
        assertThat(reservationMetrics!!.cokeNumberOfReservations.count())
            .isEqualTo(0.0)
        assertThat(reservationMetrics!!.pepsiNumberOfReservations.count())
            .isEqualTo(0.0)

        val reservation = mapOf(
            "timeSlot" to TimeSlots.NINE_AM_TO_TEN_AM.name,
            "room" to  "/rooms/1")
        mockMvc!!.perform(
            post("/reservations")
                .with(httpBasic("unknownUser", "aPassword"))
                .content(objectMapper!!.writeValueAsString(reservation))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isUnauthorized)
            .andExpect(status().reason("Unauthorized"))

        assertThat(reservationMetrics!!.cokeNumberOfReservations.count()).isEqualTo(0.0)
        assertThat(reservationMetrics!!.pepsiNumberOfReservations.count()).isEqualTo(0.0)
    }

    @Test
    @SneakyThrows
    fun `post a reservation on a busy time slot return 409`() {
        assertThat(reservationMetrics!!.cokeNumberOfReservations.count()).isEqualTo(0.0)
        assertThat(reservationMetrics!!.pepsiNumberOfReservations.count()).isEqualTo(0.0)
        val reservationOnBusySchedule = mapOf(
            "timeSlot" to TimeSlots.EIGHT_AM_TO_NINE_AM.name,
            "room" to  "/rooms/1")

        mockMvc!!.perform(post("/reservations")
            .with(httpBasic(pepsiUser!!.name, pepsiUser!!.password))
                .content(objectMapper!!.writeValueAsString(reservationOnBusySchedule))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isConflict)
            .andExpect(status().reason("The data presented are in conflict with existing ones"))
        assertThat(reservationMetrics!!.cokeNumberOfReservations.count()).isEqualTo(0.0)
        assertThat(reservationMetrics!!.pepsiNumberOfReservations.count()).isEqualTo(0.0)
    }

    @SneakyThrows
    @Test
    fun `get reservations return 200` () {

        mockMvc!!.perform(get("/reservations")
                .with(httpBasic(cokeUser!!.name, cokeUser!!.password))
                .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._embedded.reservations[0].timeSlot",
                `is`("EIGHT_AM_TO_NINE_AM")))
            .andExpect(jsonPath("$._embedded.reservations[0]._links.length()",
                `is`(4)))
            .andExpect(jsonPath("$._embedded.reservations[0]._links.self",
                notNullValue()))
            .andExpect(jsonPath("$._embedded.reservations[0]._links.reservation",
                notNullValue()))
            .andExpect(jsonPath("$._embedded.reservations[0]._links.organizer",
                notNullValue()))
            .andExpect(jsonPath("$._embedded.reservations[0]._links.room",
                notNullValue()))
            .andExpect(jsonPath("$._links.length()", `is`(3)))
            .andExpect(jsonPath("$._links.self", notNullValue()))
            .andExpect(jsonPath("$._links.profile", notNullValue()))
            .andExpect(jsonPath("$._links.search", notNullValue()))
            .andExpect(jsonPath("$.page", notNullValue()))
            .andExpect(jsonPath("$.page.size", CoreMatchers.`is`(20)))
            .andExpect(jsonPath("$.page.totalElements", `is`(1)))
            .andExpect(jsonPath("$.page.totalPages", `is`(1)))
            .andExpect(jsonPath("$.page.number", CoreMatchers.`is`(0)))
            .andDo(document("get-all-reservations-by-page"))
        }

    @Test
    @SneakyThrows
    fun `post a reservation when the quota is reached return 409`() {
        assertThat(reservationMetrics!!.cokeNumberOfReservations.count()).isEqualTo(0.0)
        assertThat(reservationMetrics!!.pepsiNumberOfReservations.count()).isEqualTo(0.0)

        val firstReservation = mapOf(
            "timeSlot" to TimeSlots.TEN_AM_TO_ELEVEN_AM.name,
            "room" to  "/rooms/10")
        mockMvc!!.perform(post("/reservations")
                .with(httpBasic(pepsiUser!!.name, pepsiUser!!.password))
                .content(objectMapper!!.writeValueAsString(firstReservation))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)
        assertThat(reservationMetrics!!.cokeNumberOfReservations.count()).isEqualTo(0.0)
        assertThat(reservationMetrics!!.pepsiNumberOfReservations.count()).isEqualTo(1.0)

        val reservation = mapOf(
            "timeSlot" to TimeSlots.TEN_AM_TO_ELEVEN_AM.name,
            "room" to  "/rooms/1")
        mockMvc!!.perform(post("/reservations")
                .with(httpBasic(pepsiUser!!.name, pepsiUser!!.password))
                .content(objectMapper!!.writeValueAsString(reservation))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isConflict)
            .andExpect(status().reason("Quota limit reached"))
            .andDo(document("create-a-reservation-quota-reached"))
        assertThat(reservationMetrics!!.cokeNumberOfReservations.count()).isEqualTo(0.0)
        assertThat(reservationMetrics!!.pepsiNumberOfReservations.count()).isEqualTo(1.0)
    }

    @SneakyThrows
    @Test
    fun `get reservations with invalid credentials return 401`() {
        assertThat(reservationMetrics!!.cokeNumberOfReservations.count()).isEqualTo(0.0)
        assertThat(reservationMetrics!!.pepsiNumberOfReservations.count()).isEqualTo(0.0)

        mockMvc!!.perform(get("/reservations")
            .with(httpBasic("unknownUser", "aPassword"))
            .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isUnauthorized)
            .andExpect(status().reason("Unauthorized"))
            assertThat(reservationMetrics!!.cokeNumberOfReservations.count()).isEqualTo(0.0)
            assertThat(reservationMetrics!!.pepsiNumberOfReservations.count()).isEqualTo(0.0)
        }

    @Test
    @SneakyThrows
    fun `cancel reservation unauthenticated_return_401`() {
        assertThat(reservationMetrics!!.cokeNumberOfReservations.count()).isEqualTo(0.0)
        assertThat(reservationMetrics!!.pepsiNumberOfReservations.count()).isEqualTo(0.0)

        mockMvc!!.perform(delete("/reservations/{id}", 1)
            .with(httpBasic("unknownUser", "aPassword"))
            .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isUnauthorized)
            .andExpect(status().reason("Unauthorized"))
        assertThat(reservationMetrics!!.cokeNumberOfReservations.count()).isEqualTo(0.0)
        assertThat(reservationMetrics!!.pepsiNumberOfReservations.count()).isEqualTo(0.0)
    }

    @Test
    @SneakyThrows
    fun `cancel reservation return_204`() {
        assertThat(reservationMetrics!!.cokeNumberOfReservations.count()).isEqualTo(0.0)
        assertThat(reservationMetrics!!.pepsiNumberOfReservations.count()).isEqualTo(0.0)

        mockMvc!!.perform(delete("/reservations/{id}", 10000)
                .with(httpBasic(cokeUser!!.name, cokeUser!!.password))
                .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNoContent)
            .andDo(document("cancel-a-reservation"))
        assertThat(reservationMetrics!!.cokeNumberOfReservations.count()).isEqualTo(0.0)
        assertThat(reservationMetrics!!.pepsiNumberOfReservations.count()).isEqualTo(0.0)
    }

    @Test
    @SneakyThrows
    fun `cancel reservation of another user return 403`() {
        assertThat(reservationMetrics!!.cokeNumberOfReservations.count()).isEqualTo(0.0)
        assertThat(reservationMetrics!!.pepsiNumberOfReservations.count()).isEqualTo(0.0)

        mockMvc!!.perform(delete("/reservations/{id}", 10000)
            .with(httpBasic(pepsiUser!!.name, pepsiUser!!.password))
            .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isForbidden)
            .andExpect(status().reason("Forbidden reservation cancellation"))
        assertThat(reservationMetrics!!.cokeNumberOfReservations.count()).isEqualTo(0.0)
        assertThat(reservationMetrics!!.pepsiNumberOfReservations.count()).isEqualTo(0.0)
    }
}
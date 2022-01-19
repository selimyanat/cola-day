package com.sy.coladay.reservation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sy.coladay.common.ITConfig;
import com.sy.coladay.common.PostgreSQLExtension;
import com.sy.coladay.user.User;
import com.sy.coladay.user.UserRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * ITest for auto generated reservation controller class.
 */
@SpringBootTest(properties = {"coke.quota=2", "pepsi.quota=1"})
@ITConfig
class ReservationControllerIT {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  UserRepository userRepository;

  User cokeUser;

  User pepsiUser;

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext,
             RestDocumentationContextProvider restDocumentation) {

    cokeUser = userRepository.findByName("user1").get();
    pepsiUser = userRepository.findByName("user2").get();
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                             .apply(documentationConfiguration(restDocumentation))
                             .apply(springSecurity())
                             .build();
  }

  @Test
  @SneakyThrows
  void postReservation_return_201() {

    final Map<String, String> reservation = new HashMap<>();
    reservation.put("timeSlot", TimeSlots.NINE_AM_TO_TEN_AM.name());
    reservation.put("room", "/rooms/1");

    mockMvc.perform(post("/reservations")
                        .with(httpBasic(cokeUser.getName(), cokeUser.getPassword()))
                        .content(objectMapper.writeValueAsString(reservation))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(header().exists("location"))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.timeSlot", is("NINE_AM_TO_TEN_AM")))
           .andExpect(jsonPath("$._links.length()", is(4)))
           .andExpect(jsonPath("$._links.self", notNullValue()))
           .andExpect(jsonPath("$._links.reservation", notNullValue()))
           .andExpect(jsonPath("$._links.organizer", notNullValue()))
           .andExpect(jsonPath("$._links.room", notNullValue()))
           .andDo(document("create-a-reservation"));
  }

  @Test
  @SneakyThrows
  void postReservation_unauthenticated_return_401() {

    final Map<String, String> reservation = new HashMap<>();
    reservation.put("timeSlot", TimeSlots.NINE_AM_TO_TEN_AM.name());
    reservation.put("room", "/rooms/1");

    mockMvc.perform(post("/reservations")
                        .with(httpBasic("unknownUser", "aPassword"))
                        .content(objectMapper.writeValueAsString(reservation))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isUnauthorized())
           .andExpect(status().reason("Unauthorized"));
  }

  @Test
  @SneakyThrows
  void postReservation_busySchedule_return_409() {

    final Map<String, String> reservationOnBusySchedule = new HashMap<>();
    reservationOnBusySchedule.put("timeSlot", TimeSlots.EIGHT_AM_TO_NINE_AM.name());
    reservationOnBusySchedule.put("room", "/rooms/1");

    mockMvc.perform(post("/reservations")
                        .with(httpBasic(pepsiUser.getName(), pepsiUser.getPassword()))
                        .content(objectMapper.writeValueAsString(reservationOnBusySchedule))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isConflict())
           .andExpect(status().reason("The data presented are in conflict with existing ones"));
  }

  @Test
  @SneakyThrows
  void postReservation_quotaReached_return_409() {

    final Map<String, String> firstReservation = new HashMap<>();
    firstReservation.put("timeSlot", TimeSlots.TEN_AM_TO_ELEVEN_AM.name());
    firstReservation.put("room", "/rooms/10");

    mockMvc.perform(post("/reservations")
                        .with(httpBasic(pepsiUser.getName(), pepsiUser.getPassword()))
                        .content(objectMapper.writeValueAsString(firstReservation))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isCreated());

    final Map<String, String> reservation = new HashMap<>();
    reservation.put("timeSlot", TimeSlots.TEN_AM_TO_ELEVEN_AM.name());
    reservation.put("room", "/rooms/1");

    mockMvc.perform(post("/reservations")
                        .with(httpBasic(pepsiUser.getName(), pepsiUser.getPassword()))
                        .content(objectMapper.writeValueAsString(reservation))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isConflict())
           .andExpect(status().reason("Quota limit reached"))
           .andDo(document("create-a-reservation-quota-reached"));
  }

  @Test
  @SneakyThrows
  void getReservations_return_200() {

    mockMvc.perform(get("/reservations")
                        .with(httpBasic(cokeUser.getName(), cokeUser.getPassword()))
                        .accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(jsonPath("$._embedded.reservations[0].timeSlot",
                               is("EIGHT_AM_TO_NINE_AM")
           ))
           .andExpect(jsonPath("$._embedded.reservations[0]._links.length()", is(4)))
           .andExpect(jsonPath("$._embedded.reservations[0]._links.self", notNullValue()))
           .andExpect(jsonPath("$._embedded.reservations[0]._links.reservation", notNullValue()))
           .andExpect(jsonPath("$._embedded.reservations[0]._links.organizer", notNullValue()))
           .andExpect(jsonPath("$._embedded.reservations[0]._links.room", notNullValue()))
           .andExpect(jsonPath("$._links.length()", is(3)))
           .andExpect(jsonPath("$._links.self", notNullValue()))
           .andExpect(jsonPath("$._links.profile", notNullValue()))
           .andExpect(jsonPath("$._links.search", notNullValue()))
           .andExpect(jsonPath("$.page", notNullValue()))
           .andExpect(jsonPath("$.page.size", is(20)))
           .andExpect(jsonPath("$.page.totalElements", is(1)))
           .andExpect(jsonPath("$.page.totalPages", is(1)))
           .andExpect(jsonPath("$.page.number", is(0)))
           .andDo(document("get-all-reservations-by-page"));
    ;
  }

  @Test
  @SneakyThrows
  void getReservations_unauthenticated_return_401() {

    mockMvc.perform(get("/reservations")
                        .with(httpBasic("unknownUser", "aPassword"))
                        .accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isUnauthorized())
           .andExpect(status().reason("Unauthorized"));
  }

  @Test
  @SneakyThrows
  void cancelReservation_unauthenticated_return_401() {

    mockMvc.perform(delete("/reservations/{id}", 1)
                        .with(httpBasic("unknownUser", "aPassword"))
                        .accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isUnauthorized())
           .andExpect(status().reason("Unauthorized"));
  }

  @Test
  @SneakyThrows
  void cancelReservation_return_204() {

    mockMvc.perform(delete("/reservations/{id}", 10000)
                        .with(httpBasic(cokeUser.getName(), cokeUser.getPassword()))
                        .accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isNoContent())
           .andDo(document("cancel-a-reservation"));
  }

  @Test
  @SneakyThrows
  void cancelReservation_ofAnotherUser_return_403() {

    mockMvc.perform(delete("/reservations/{id}", 10000)
                        .with(httpBasic(pepsiUser.getName(), pepsiUser.getPassword()))
                        .accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isForbidden())
           .andExpect(status().reason("Forbidden reservation cancellation"));
  }
}

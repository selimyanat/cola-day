package com.sy.coladay.room;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sy.coladay.common.ITConfig;
import com.sy.coladay.user.User;
import com.sy.coladay.user.UserRepository;
import java.util.Collections;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * ITest for auto generated room controller class.
 */
@SpringBootTest
@ITConfig
class RoomControllerIT {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  UserRepository userRepository;

  User cokeUser;

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext,
             RestDocumentationContextProvider restDocumentation) {

    cokeUser = userRepository.findByName("user1").get();
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                             .apply(documentationConfiguration(restDocumentation))
                             .apply(springSecurity())
                             .build();
  }

  @Test
  @SneakyThrows
  void postRoom_return_405() {

    mockMvc.perform(post("/rooms")
                        .with(httpBasic(cokeUser.getName(), cokeUser.getPassword()))
                        .content(objectMapper.writeValueAsString(Collections.EMPTY_MAP))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isMethodNotAllowed());
  }

  @Test
  @SneakyThrows
  void deleteRoom_return_405() {

    mockMvc.perform(delete("/rooms/{id}", 1)
                        .with(httpBasic(cokeUser.getName(), cokeUser.getPassword()))
                        .accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isMethodNotAllowed());
  }

  @Test
  @SneakyThrows
  void getRooms_return_200() {

    mockMvc.perform(get("/rooms?size=1")
                        .with(httpBasic(cokeUser.getName(), cokeUser.getPassword()))
                        .accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(jsonPath("$._embedded.rooms[0].name",
                               is("COKE_R01")
           ))
           .andExpect(jsonPath("$._embedded.rooms[0].owner",
                               is("COKE")
           ))
           .andExpect(jsonPath("$._embedded.rooms[0]._links.length()", is(3)))
           .andExpect(jsonPath("$._embedded.rooms[0]._links.self", notNullValue()))
           .andExpect(jsonPath("$._embedded.rooms[0]._links.room", notNullValue()))
           .andExpect(jsonPath("$._embedded.rooms[0]._links.reservations", notNullValue()))
           .andExpect(jsonPath("$._links.length()", is(5)))
           .andExpect(jsonPath("$._links.self", notNullValue()))
           .andExpect(jsonPath("$._links.first", notNullValue()))
           .andExpect(jsonPath("$._links.next", notNullValue()))
           .andExpect(jsonPath("$._links.last", notNullValue()))
           .andExpect(jsonPath("$._links.profile", notNullValue()))
           .andExpect(jsonPath("$.page", notNullValue()))
           .andExpect(jsonPath("$.page.size", is(1)))
           .andExpect(jsonPath("$.page.totalElements", is(20)))
           .andExpect(jsonPath("$.page.totalPages", is(20)))
           .andExpect(jsonPath("$.page.number", is(0)))
           .andDo(document("list-all-rooms"));
  }

  @Test
  @SneakyThrows
  void getRooms_unauthenticated_return_401() {

    mockMvc.perform(get("/rooms")
                        .with(httpBasic("unknownUser", "aPassword"))
                        .accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isUnauthorized())
           .andExpect(status().reason("Unauthorized"));
  }

}

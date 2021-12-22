package com.sy.coladay.user;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class UserControllerIT {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  UserRepository userRepository;

  User cokeUser;

  @BeforeEach
  public void setUp() {
    cokeUser = userRepository.findById(1L).get();
  }

  @Test
  @SneakyThrows
  void postUser_return_405() {

    mockMvc.perform(post("/users")
        .with(httpBasic(cokeUser.getName(), cokeUser.getPassword()))
        .content(objectMapper.writeValueAsString(Collections.EMPTY_MAP))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isMethodNotAllowed());
  }

  @Test
  @SneakyThrows
  void deleteUser_return_405() {

    mockMvc.perform(delete("/users/{id}", 1)
        .with(httpBasic(cokeUser.getName(), cokeUser.getPassword()))
        .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isMethodNotAllowed());
  }

  @Test
  @SneakyThrows
  void getUsers_return_200() {

    mockMvc.perform(get("/users")
        .with(httpBasic(cokeUser.getName(), cokeUser.getPassword()))
        .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.users[0].name",
            is("user1")))
        .andExpect(jsonPath("$._embedded.users[0].company",
            is("COKE")))
        .andExpect(jsonPath("$._embedded.users[0]._links.length()", is(2)))
        .andExpect(jsonPath("$._embedded.users[0]._links.self", notNullValue()))
        .andExpect(jsonPath("$._embedded.users[0]._links.user", notNullValue()))
        .andExpect(jsonPath("$._embedded.users[1].name",
            is("user2")))
        .andExpect(jsonPath("$._embedded.users[1].company",
            is("PEPSI")))
        .andExpect(jsonPath("$._embedded.users[1]._links.length()", is(2)))
        .andExpect(jsonPath("$._embedded.users[1]._links.self", notNullValue()))
        .andExpect(jsonPath("$._embedded.users[1]._links.user", notNullValue()))
        .andExpect(jsonPath("$._links.length()", is(2)))
        .andExpect(jsonPath("$._links.profile", notNullValue()));
  }

  @Test
  @SneakyThrows
  void getUsers_unauthenticated_return_401() {

    mockMvc.perform(get("/users")
        .with(httpBasic("unknownUser", "aPassword"))
        .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(status().reason("Unauthorized"));
  }

}

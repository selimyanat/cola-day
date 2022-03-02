package com.sy.coladay.metrics;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sy.coladay.common.ITConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration tests for metrics exposed through spring actuator. NOTE:
 * The test configuration override the configured spring boot actuator port in application
 * .properties as the test is using MockMvc which runs an embedded http.
 *  For Production usage, the actuator endpoints are exposed by default on 8081. This can be
 *  changed by updating the configuration property `management.server.port` in
 *  application.properties.
 */
@SpringBootTest(properties = { "management.server.port="})
@ITConfig
public class MetricsControllerIT {

  @Autowired
  MockMvc mockMvc;

  @BeforeEach
  public void setUp(WebApplicationContext webApplicationContext,
                    RestDocumentationContextProvider restDocumentation) {

    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                             .apply(documentationConfiguration(restDocumentation))
                             .apply(springSecurity())
                             .build();
  }

  @Test
  @SneakyThrows
  public void getActuator_return_200() {

    mockMvc.perform(get("/actuator/")
                        .accept(MediaType.APPLICATION_JSON))
           .andDo(print())
           .andExpect(status().isOk())
           // In case, we upgrade spring boot this test can catch regression and new functionality
           .andExpect(jsonPath("$._links.length()", is(6)))
           .andExpect(jsonPath("$._links.health-component-instance", notNullValue()))
           .andExpect(jsonPath("$._links.health-component", notNullValue()))
           .andExpect(jsonPath("$._links.health", notNullValue()))
           .andExpect(jsonPath("$._links.metrics", notNullValue()))
           .andExpect(jsonPath("$._links.metrics-requiredMetricName", notNullValue()))
           .andDo(document("list-all-metrics-endpoints"))
    ;
  }

  @Test
  @SneakyThrows
  public void getMetrics_return_200() {

    mockMvc.perform(get("/actuator/metrics")
                        .accept(MediaType.APPLICATION_JSON))
           .andDo(print())
           .andExpect(status().isOk())
           // In case, we upgrade spring boot this test can catch regression and new functionality
           .andExpect(jsonPath("$.names", hasSize(39)))
           // watch out the key metrics exposed to the monitoring tool to not break their
           // integration in case of an upgrade of spring boot.
           .andExpect(jsonPath("$.names", hasItem("jvm.threads.states")))
           .andExpect(jsonPath("$.names", hasItem("jdbc.connections.active")))
           .andExpect(jsonPath("$.names", hasItem("jvm.memory.used")))
           .andExpect(jsonPath("$.names", hasItem("jvm.memory.max")))
           .andExpect(jsonPath("$.names", hasItem("jdbc.connections.min")))
           .andExpect(jsonPath("$.names", hasItem("jdbc.connections.max")))
           .andExpect(jsonPath("$.names", hasItem("jdbc.connections.max")))
           .andExpect(jsonPath("$.names", hasItem("system.cpu.usage")))
           .andExpect(jsonPath("$.names", hasItem("hikaricp.connections.max")))
           .andExpect(jsonPath("$.names", hasItem("hikaricp.connections.min")))
           .andExpect(jsonPath("$.names", hasItem("hikaricp.connections.idle")))
           .andExpect(jsonPath("$.names", hasItem("hikaricp.connections.pending")))
           .andExpect(jsonPath("$.names", hasItem("hikaricp.connections.usage")))
           .andExpect(jsonPath("$.names", hasItem("process.uptime")))
           .andExpect(jsonPath("$.names", hasItem("process.cpu.usage")))
           .andExpect(jsonPath("$.names", hasItem("process.start.time")))
           .andDo(document("list-all-applications-metrics"))
    ;

  }

  @Test
  @SneakyThrows
  public void getHealth_return_200() {

    mockMvc.perform(get("/actuator/health")
                        .accept(MediaType.APPLICATION_JSON))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.status", is("UP")))
           .andDo(document("read-application-health"));
  }

}

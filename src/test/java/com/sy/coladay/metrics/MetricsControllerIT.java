package com.sy.coladay.metrics;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
 * Integration tests for metrics exposed through spring actuator. NOTE: The test configuration
 * override the configured spring boot actuator port in application .properties as the test is using
 * MockMvc which runs an embedded http. For Production usage, the actuator endpoints are exposed by
 * default on 8081. This can be changed by updating the configuration property
 * `management.server.port` in application.properties.
 */
@SpringBootTest(properties = {"management.server.port="})
@ITConfig
class MetricsControllerIT {

  @Autowired
  MockMvc mockMvc;

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext,
             RestDocumentationContextProvider restDocumentation) {

    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                             .apply(documentationConfiguration(restDocumentation))
                             .apply(springSecurity()).build();
  }

  @Test
  @SneakyThrows
  void getActuator_return_200() {

    mockMvc.perform(get("/actuator/").accept(MediaType.APPLICATION_JSON)).andDo(print())
           .andExpect(status().isOk())
           // In case, we upgrade spring boot or its configuration, this test can catch regression
           // and new functionality
           .andExpect(jsonPath("$._links.length()", is(7)))
           .andExpect(jsonPath("$._links.health-component-instance", notNullValue()))
           .andExpect(jsonPath("$._links.health-component", notNullValue()))
           .andExpect(jsonPath("$._links.health", notNullValue()))
           .andExpect(jsonPath("$._links.metrics", notNullValue()))
           .andExpect(jsonPath("$._links.metrics-requiredMetricName", notNullValue()))
           .andExpect(jsonPath("$._links.prometheus", notNullValue()))
           .andDo(document("list-all-metrics-endpoints"));
  }

  @Test
  @SneakyThrows
  void getMetrics_return_200() {

    mockMvc.perform(get("/actuator/metrics").accept(MediaType.APPLICATION_JSON)).andDo(print())
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
           .andExpect(jsonPath("$.names", hasItem("jdbc.connections.active")))
           .andExpect(jsonPath("$.names", hasItem("system.cpu.usage")))
           .andExpect(jsonPath("$.names", hasItem("process.uptime")))
           .andExpect(jsonPath("$.names", hasItem("process.cpu.usage")))
           .andExpect(jsonPath("$.names", hasItem("process.start.time")))
           .andDo(document("list-all-applications-metrics"));

  }

  @Test
  @SneakyThrows
  void getHealth_return_200() {

    mockMvc.perform(get("/actuator/health").accept(MediaType.APPLICATION_JSON)).andDo(print())
           .andExpect(status().isOk()).andExpect(jsonPath("$.status", is("UP")))
           .andDo(document("read-application-health"));
  }

  @Test
  @SneakyThrows
  void getPrometheusMetrics_returns200() {

    mockMvc.perform(get("/actuator/prometheus").accept(MediaType.TEXT_PLAIN_VALUE)).andDo(print())
           // NOTE: This is a use case for consumer contract testing!
           .andExpect(status().isOk()).andExpect(content().string(containsString(
               "jvm_memory_used_bytes{application=\"coladay\",area=\"heap\","
                   + "id=\"G1 Eden Space\",}")))
           .andExpect(content().string(containsString(
               "jvm_memory_used_bytes{application=\"coladay\",area=\"heap\","
                   + "id=\"G1 Old Gen\",}")))
           .andExpect(content().string(containsString(
               "jvm_memory_used_bytes{application=\"coladay\",area=\"nonheap\","
                   + "id=\"Metaspace\",}")))
           .andExpect(content().string(containsString(
               "jvm_memory_used_bytes{application=\"coladay\",area=\"heap\","
                   + "id=\"G1 Survivor Space\",}")))
           .andExpect(content().string(containsString(
               "jvm_memory_used_bytes{application=\"coladay\",area=\"nonheap\","
                   + "id=\"CodeHeap 'non-nmethods'\",}")))
           .andExpect(content().string(containsString(
               "jvm_memory_used_bytes{application=\"coladay\",area=\"nonheap\","
                   + "id=\"CodeHeap 'non-profiled nmethods'\",}")))
           .andExpect(content().string(containsString(
               "jvm_memory_used_bytes{application=\"coladay\",area=\"nonheap\","
                   + "id=\"Compressed Class Space\",}")))
           .andExpect(content().string(containsString(
               "jvm_memory_used_bytes{application=\"coladay\",area=\"nonheap\","
                   + "id=\"CodeHeap 'profiled nmethods'\",}")))
           .andExpect(
               content().string(containsString(
                   "system_cpu_usage{application=\"coladay\",}")))
           .andExpect(content().string(containsString(
               "jdbc_connections_min{application=\"coladay"
                   + "\",name=\"dataSource\",}")))
           .andExpect(content().string(containsString(
               "jdbc_connections_max{application=\"coladay"
                   + "\",name=\"dataSource\",}")))
           .andExpect(content().string(containsString(
               "jdbc_connections_active{application=\"coladay"
                   + "\",name=\"dataSource\",}")))
           .andExpect(content().string(containsString(
               "jvm_threads_states_threads{application"
                   + "=\"coladay\",state=\"runnable\",}")))
           .andExpect(content().string(containsString(
               "jvm_threads_states_threads{application"
                   + "=\"coladay\",state=\"blocked\",}")))
           .andExpect(content().string(containsString(
               "jvm_threads_states_threads"
                   + "{application=\"coladay\",state=\"waiting\",}")))
           .andExpect(content().string(containsString(
               "jvm_threads_states_threads{application"
                   + "=\"coladay\",state=\"timed-waiting\",}")))
           .andExpect(content().string(containsString(
               "jvm_threads_states_threads{application"
                   + "=\"coladay\",state=\"new\",}")))
           .andExpect(content().string(containsString(
               "process_uptime_seconds")))
           .andExpect(content().string(containsString(
               "process_cpu_usage")))
           .andExpect(content().string(containsString(
               "process_start_time_seconds")))
           .andDo(document("list-all-applications-metrics-in-prometheus-format"));
  }

}
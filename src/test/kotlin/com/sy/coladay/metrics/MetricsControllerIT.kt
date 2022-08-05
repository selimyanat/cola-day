package com.sy.coladay.metrics

import com.sy.coladay.common.ITConfig
import lombok.SneakyThrows
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

/**
 * Integration tests for metrics exposed through spring actuator. NOTE: The test configuration
 * override the configured spring boot actuator port in application .properties as the test is using
 * MockMvc which runs an embedded http. For Production usage, the actuator endpoints are exposed by
 * default on 8081. This can be changed by updating the configuration property
 * `management.server.port` in application.properties.
 */
@SpringBootTest(properties = ["management.server.port="])
@ITConfig
internal class MetricsControllerIT {

    @Autowired
    var mockMvc: MockMvc? = null

    @BeforeEach
    fun setUp(webApplicationContext: WebApplicationContext?,
              restDocumentation: RestDocumentationContextProvider?) {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply<DefaultMockMvcBuilder>(documentationConfiguration(restDocumentation))
            .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
            .build()
    }

    // In case, we upgrade spring boot or its configuration, this test can catch regression
    // and new functionality
    @SneakyThrows
    @Test
    fun `hit actuator root endpoint return 200`() {

            mockMvc!!.perform(get("/actuator/")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk)
                // In case, we upgrade spring boot or its configuration, this test can catch
                // regression and new functionality
                .andExpect(jsonPath("$._links.length()", `is`(6)))
                .andExpect(jsonPath("$._links.self", notNullValue()))
                .andExpect(jsonPath("$._links.health-path", notNullValue()))
                .andExpect(jsonPath("$._links.health", notNullValue()))
                .andExpect(jsonPath("$._links.prometheus", notNullValue()))
                .andExpect(jsonPath("$._links.metrics", notNullValue()))
                .andExpect(jsonPath("$._links.metrics-requiredMetricName", notNullValue()))
                .andDo(document("list-all-metrics-endpoints"))
        }

    // In case, we upgrade spring boot this test can catch regression and new functionality
    @SneakyThrows
    @Test
    fun `hit metrics endpoint return 200`() {

            mockMvc!!.perform(get("/actuator/metrics")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk)
                // In case, we upgrade spring boot this test can catch regression and new
                // functionality
                .andExpect(jsonPath<Iterable<String?>>("$.names",
                    hasItem("jvm.threads.states")))
                .andExpect(jsonPath<Iterable<String?>>("$.names",
                    hasItem("jdbc.connections.active")))
                .andExpect(jsonPath<Iterable<String?>>("$.names",
                    hasItem("jvm.memory.used")))
                .andExpect(jsonPath<Iterable<String?>>("$.names",
                    hasItem("jvm.memory.max")))
                .andExpect(jsonPath<Iterable<String?>>("$.names",
                    hasItem("jdbc.connections.min")))
                .andExpect(jsonPath<Iterable<String?>>("$.names",
                    hasItem("jdbc.connections.max")))
                .andExpect(jsonPath<Iterable<String?>>("$.names",
                    hasItem("jdbc.connections.active")))
                .andExpect(jsonPath<Iterable<String?>>("$.names",
                    hasItem("system.cpu.usage")))
                .andExpect(jsonPath<Iterable<String?>>("$.names",
                    hasItem("process.uptime")))
                .andExpect(jsonPath<Iterable<String?>>("$.names",
                    hasItem("process.cpu.usage")))
                .andExpect(jsonPath<Iterable<String?>>("$.names",
                    hasItem("process.start.time")))
                .andDo(document("list-all-applications-metrics"))
        }

    @SneakyThrows
    @Test
    fun `hit health endpoint return 200`() {

        mockMvc!!.perform(get("/actuator/health")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status", CoreMatchers.`is`("UP")))
            .andExpect(jsonPath("$.groups", hasItems("liveness", "readiness")))
            .andDo(document("read-aggregated-application-health"))
        }

    @SneakyThrows
    @Test
    fun `hit readiness probe endpoint return 200`() {

        mockMvc!!.perform(get("/actuator/health/readiness")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status", `is`("UP")))
            .andDo(document("read-application-readiness"))
    }

    @SneakyThrows
    @Test
    fun `hit liveness probe endpoint return 200`() {

        mockMvc!!.perform(get("/actuator/health/liveness")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status", `is`("UP")))
            .andDo(document("read-application-liveness"))
    }

    // NOTE: This is a use case for consumer contract testing!
    @SneakyThrows
    @Test
    fun `hit prometheus endpoint returns 200`() {

        mockMvc!!.perform(get("/actuator/prometheus")
            .accept(MediaType.TEXT_PLAIN_VALUE))
            .andDo(MockMvcResultHandlers.print())
            // NOTE: This is a use case for consumer contract testing!
            .andExpect(status().isOk)
            .andExpect(content().string(containsString("number_of_reservations_total" +
                    "{application=\"coladay\",owner=\"coke\",}")))
            .andExpect(content().string(containsString("number_of_reservations_total" +
                    "{application=\"coladay\",owner=\"pepsi\",}")))
            .andExpect(content().string(containsString("jvm_memory_used_bytes" +
                    "{application=\"coladay\",area=\"heap\",id=\"G1 Eden Space\",}")))
            .andExpect(content().string(containsString("jvm_memory_used_bytes" +
                    "{application=\"coladay\",area=\"heap\",id=\"G1 Old Gen\",}")))
            .andExpect(content().string(containsString("jvm_memory_used_bytes" +
                    "{application=\"coladay\",area=\"nonheap\",id=\"Metaspace\",}")))
            .andExpect(content().string(containsString("jvm_memory_used_bytes" +
                    "{application=\"coladay\",area=\"heap\",id=\"G1 Survivor Space\",}")))
            .andExpect(content().string(containsString("jvm_memory_used_bytes" +
                    "{application=\"coladay\",area=\"nonheap\",id=\"CodeHeap 'non-nmethods'\",}")))
            .andExpect(content().string(containsString("jvm_memory_used_bytes" +
                    "{application=\"coladay\",area=\"nonheap\",id=\"CodeHeap 'non-profiled " +
                    "nmethods'\",}")))
            .andExpect(content().string(containsString("jvm_memory_used_bytes" +
                    "{application=\"coladay\",area=\"nonheap\",id=\"Compressed Class Space\",}")))
            .andExpect(content().string(containsString("jvm_memory_used_bytes" +
                    "{application=\"coladay\",area=\"nonheap\"," +
                    "id=\"CodeHeap 'profiled nmethods'\",}")))
            .andExpect(content().string(containsString("system_cpu_usage" +
                    "{application=\"coladay\",}")))
            .andExpect(content().string(containsString("jdbc_connections_min" +
                    "{application=\"coladay" + "\",name=\"dataSource\",}")))
            .andExpect(content().string(containsString("jdbc_connections_max" +
                    "{application=\"coladay" + "\",name=\"dataSource\",}")))
            .andExpect(content().string(containsString("jdbc_connections_active" +
                    "{application=\"coladay" + "\",name=\"dataSource\",}")))
            .andExpect(content().string(containsString("jvm_threads_states_threads" +
                    "{application" + "=\"coladay\",state=\"runnable\",}")))
            .andExpect(content().string(containsString("jvm_threads_states_threads" +
                    "{application" + "=\"coladay\",state=\"blocked\",}")))
            .andExpect(content().string(containsString("jvm_threads_states_threads"
                                    + "{application=\"coladay\",state=\"waiting\",}")))
            .andExpect(content().string(containsString("jvm_threads_states_threads" +
                    "{application" + "=\"coladay\",state=\"timed-waiting\",}")))
            .andExpect(content().string(containsString("jvm_threads_states_threads" +
                    "{application" + "=\"coladay\",state=\"new\",}")))
            .andExpect(content().string(containsString("process_uptime_seconds")))
            .andExpect(content().string(containsString("process_cpu_usage")))
            .andExpect(content().string(containsString("process_start_time_seconds")))
            .andDo(document("list-all-applications-metrics-in-prometheus-format"))
        }
}
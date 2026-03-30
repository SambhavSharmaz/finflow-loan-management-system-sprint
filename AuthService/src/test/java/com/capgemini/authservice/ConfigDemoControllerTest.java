package com.capgemini.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@WebMvcTest(controllers = ConfigDemoController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        properties = {"finflow.message=TestMessage", "spring.cloud.config.enabled=false", "spring.config.import="})
public class ConfigDemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetMessage() throws Exception {
        mockMvc.perform(get("/demo-config"))
                .andExpect(status().isOk())
                .andExpect(content().string("Message from GitHub Config: TestMessage"));
    }
}

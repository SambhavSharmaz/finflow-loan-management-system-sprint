package com.capgemini.authservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConfigDemoControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ConfigDemoController controller = new ConfigDemoController();
        ReflectionTestUtils.setField(controller, "message", "TestMessage");
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetMessage() throws Exception {
        mockMvc.perform(get("/demo-config"))
                .andExpect(status().isOk())
                .andExpect(content().string("Message from GitHub Config: TestMessage"));
    }
}

package com.nextbuy.platform.test.web;


import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.json.JsonMapper;

public abstract class ControllerTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JsonMapper jsonMapper;

    @Autowired
    protected WebApplicationContext context;

    protected MockMvcUtils mockMvcUtils;


    @BeforeEach
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mockMvcUtils = new MockMvcUtils(mockMvc, jsonMapper);
    }
}

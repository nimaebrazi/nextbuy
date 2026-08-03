package com.nextbuy.adhub.support.ad.controller;


import com.nextbuy.adhub.bootstrap.config.LocaleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest
@Import(LocaleConfig.class)
public class ControllerTestBase {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JsonMapper jsonMapper;

    @Autowired
    WebApplicationContext context;

    protected MockMvcUtils mockMvcUtils;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mockMvcUtils = new MockMvcUtils(mockMvc, jsonMapper);
    }
}

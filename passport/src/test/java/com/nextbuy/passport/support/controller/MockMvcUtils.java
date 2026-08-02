package com.nextbuy.passport.support.controller;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class MockMvcUtils {
    private final MockMvc mockMvc;
    private final JsonMapper objectMapper;

    public MockMvcUtils(MockMvc mockMvc, JsonMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }


    public ResultActions performPost(String url, Object requestBody) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
        );
    }

    public ResultActions performGet(String url, Object... uriVars) throws Exception {
        return mockMvc.perform(get(url, uriVars));
    }

    public ResultActions performPut(String url, Object requestBody) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
        );
    }

    public ResultActions performDelete(String url) throws Exception {
        return mockMvc.perform(delete(url));
    }

    public ResultActions performDelete(String url, Object... uriVars) throws Exception {
        return mockMvc.perform(delete(url, uriVars));
    }


}

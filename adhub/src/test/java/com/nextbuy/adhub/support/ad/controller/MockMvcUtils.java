package com.nextbuy.adhub.support.ad.controller;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.json.JsonMapper;

public class MockMvcUtils {

    public static final String HEADER_USER_ID = "X-User-Id";

    private final MockMvc mockMvc;
    private final JsonMapper jsonMapper;

    public MockMvcUtils(MockMvc mockMvc, JsonMapper jsonMapper) {
        this.mockMvc = mockMvc;
        this.jsonMapper = jsonMapper;
    }

    public RequestSpec post(String url, Object... uriVars) {
        return new RequestSpec(MockMvcRequestBuilders.post(url, uriVars));
    }

    public RequestSpec get(String url, Object... uriVars) {
        return new RequestSpec(MockMvcRequestBuilders.get(url, uriVars));
    }

    public RequestSpec put(String url, Object... uriVars) {
        return new RequestSpec(MockMvcRequestBuilders.put(url, uriVars));
    }

    public RequestSpec delete(String url, Object... uriVars) {
        return new RequestSpec(MockMvcRequestBuilders.delete(url, uriVars));
    }

    public final class RequestSpec {
        private final MockHttpServletRequestBuilder request;

        private RequestSpec(MockHttpServletRequestBuilder request) {
            this.request = request;
        }

        public RequestSpec userId(long userId) {
            request.header(HEADER_USER_ID, userId);
            return this;
        }

        public RequestSpec header(String name, Object value) {
            request.header(name, value);
            return this;
        }

        public RequestSpec body(Object body) throws Exception {
            request.contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(body));
            return this;
        }

        public ResultActions exchange() throws Exception {
            return mockMvc.perform(request);
        }
    }
}
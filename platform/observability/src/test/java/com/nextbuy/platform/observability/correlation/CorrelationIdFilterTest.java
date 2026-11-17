package com.nextbuy.platform.observability.correlation;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();


    @AfterEach
    public void clearMDC(){
        MDC.clear();
    }


    @Test
    void usesIncomingHeaderAndEchoesIt() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader(CorrelationIdFilter.HEADER, "req-123");
        var response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(response.getHeader(CorrelationIdFilter.HEADER)).isEqualTo("req-123");
        assertThat(MDC.get(CorrelationIdFilter.MDC_KEY)).isNull();
        verify(chain).doFilter(request, response);
    }


    @Test
    void generatesIdWhenHeaderMissing() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        FilterChain chain = (req, res) ->
                assertThat(MDC.get(CorrelationIdFilter.MDC_KEY)).isNotBlank();

        filter.doFilter(request, response, chain);

        assertThat(response.getHeader(CorrelationIdFilter.HEADER)).isNotBlank();
    }
}

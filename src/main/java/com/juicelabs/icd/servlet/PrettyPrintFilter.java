package com.juicelabs.icd.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Pretty print when user agent is a web browser.
 */


@Component
//@Order(2)
public class PrettyPrintFilter implements Filter {
    private FilterConfig config;

    private ObjectMapper mapper = new ObjectMapper();

    public void init(FilterConfig config) throws ServletException {
        this.config = config;
    }

    public final void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletRequest reconstructedRequest = getRequest(request);

        ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) servletResponse);
        filterChain.doFilter(reconstructedRequest, responseWrapper);
        if (isBrowser(request) && hasContent(responseWrapper) && isJsonResponse(responseWrapper)) {
            String body = responseWrapper.getCaptureAsString();

            Object json = mapper.readValue(body, Object.class);
            String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            responseWrapper.commitResponse(pretty);
        } else {
            responseWrapper.commitResponse(responseWrapper.getCaptureAsBytes());
        }
    }

    // hacky but works for this demo.
    private boolean isBrowser(HttpServletRequest request) {
        return request.getHeader("user-agent") != null ? request.getHeader("user-agent").contains("Moz") : false;
    }

    private boolean hasContent(ResponseWrapper wrapper) {
        return wrapper.getStatus() != NO_CONTENT.value();
    }

    private boolean isJsonResponse(HttpServletResponse response) {
        return response.getContentType().contains(APPLICATION_JSON_VALUE);
    }

    private HttpServletRequest getRequest(final HttpServletRequest request) {
        return new HttpServletRequestWrapper(request);
    }

    @Override
    public void destroy() {
        // not needed
    }
}


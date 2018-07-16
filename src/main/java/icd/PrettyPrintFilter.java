package icd;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Component
//@Order(2)
public class PrettyPrintFilter implements Filter {
    protected FilterConfig config;

    @Autowired
    ObjectMapper mapper;

    public void init(FilterConfig config) throws ServletException {
        this.config = config;
    }
//    @Override
//    public void doFilter(
//            ServletRequest request,
//            ServletResponse response,
//            FilterChain chain) throws IOException, ServletException {
//
//        HttpServletRequest req = (HttpServletRequest) request;
//        HttpServletResponse res = (HttpServletResponse) response;
////        LOG.info(
////                "Logging Request  {} : {}", req.getMethod(),
////                req.getRequestURI());
//        chain.doFilter(request, response);
////        LOG.info(
////                "Logging Response :{}",
////                res.getContentType());
//        System.out.println(res.getContentType());
//    }
//
//    @Override
//    public void destroy() {
//
//    }


    //    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
    public final void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            doFilter2(servletRequest, servletResponse, filterChain);
        } catch (RuntimeException cie) {
            HttpServletResponse resp = (HttpServletResponse) servletResponse;
            //            writeErrorResponse(resp, cie);
        }
    }


    private void doFilter2(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//            HttpServletRequest newRequest = doPreFilter(request, response);

        HttpServletRequest reconstructedRequest = getRequest(request);
        ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) servletResponse);
//            reconstructedRequest.startAsync().addListener(new AsyncFilterListener(newRequest, responseWrapper, this));
        filterChain.doFilter(reconstructedRequest, responseWrapper);
        if (hasContent(responseWrapper) && isJsonResponse(responseWrapper)) {
            String body = responseWrapper.getCaptureAsString();
            String transformedBody = body;

            Object json = mapper.readValue(body, Object.class);
            String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            responseWrapper.commitResponse(pretty);
        } else {
            responseWrapper.commitResponse(responseWrapper.getCaptureAsBytes());
        }
    }

    private boolean hasContent(ResponseWrapper wrapper) {
        return wrapper.getStatus() != NO_CONTENT.value();
    }

    private boolean isJsonResponse(HttpServletResponse response) {
        return response.getContentType().contains(APPLICATION_JSON_VALUE);
    }

//        @Override
//        public void destroy() {
//            // no op
//        }

    private HttpServletRequest getRequest(final HttpServletRequest request) {
        return new HttpServletRequestWrapper(request);
    }

    //        ServletResponse newResponse = response;
//
//        if (request instanceof HttpServletRequest) {
//            newResponse = new CharResponseWrapper((HttpServletResponse) response);
//        }
//
//        chain.doFilter(request, newResponse);
//
//        if (newResponse instanceof CharResponseWrapper) {
//            String text = newResponse.toString();
//            if (text != null) {
//                text = text.toUpperCase();
//                response.getWriter().write(text);
//            }
//        }
//    }

    @Override
    public void destroy() {

    }
}


// other methods
//}
////@WebFilter
//public class PrettyPrintFilter implements Filter {
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) servletResponse);
//
//        filterChain.doFilter(servletRequest, responseWrapper);
//
//        String responseContent = new String(responseWrapper.getDataStream());
//
////        RestResponse fullResponse = new RestResponse(/*status*/, /*message*/,responseContent);
////
////        byte[] responseToSend = restResponseBytes(fullResponse);
////
////        response.getOutputStream().write(responseToSend);
//    }
//
//    @Override
//    public void destroy() {
//
//    }
//}

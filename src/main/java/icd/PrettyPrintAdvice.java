package icd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Response;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//@ControllerAdvice
public class PrettyPrintAdvice implements ResponseBodyAdvice<Response<?>> {

    private static final String FILTER_KEY = "filter";

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
//        return methodParameter.hasMethodAnnotation(RestFilter.class);
        return true;
    }



    @Override
    public Response<?> beforeBodyWrite(Response<?> list,
                                        MethodParameter methodParameter,
                                        MediaType mediaType,
                                        Class<? extends HttpMessageConverter<?>> aClass,
                                        ServerHttpRequest serverHttpRequest,
                                        ServerHttpResponse serverHttpResponse) {

        //log.info("list = {}", list);
        HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
        // check whether the request has filter key or not
        if (httpServletRequest.getParameterMap().containsKey(FILTER_KEY)) {
//            log.info("RestFilter: triggered");
            Set<String> filterKeys = new HashSet<>(Arrays.asList(httpServletRequest.getParameter(FILTER_KEY).split(",")));
//            log.info("RestFilter: filter keys = {}", filterKeys);
//
            if (filterKeys.isEmpty()) {
//                log.info("RestFilter: filter nothing since filter key is empty");
                return list;
            }

//
        }
        return list;
    }

}

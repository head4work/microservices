package com.head4work.companyservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignHeaderPropagationConfig {

    @Bean
    public RequestInterceptor propagateHeadersInterceptor() {
        return template -> {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
                var request = servletRequestAttributes.getRequest();
                String userId = request.getHeader("X-User-ID");
                String roles = request.getHeader("X-User-Roles");

                if (userId != null) {
                    template.header("X-User-ID", userId);
                }
                if (roles != null) {
                    template.header("X-User-Roles", roles);
                }
            }
        };
    }
}
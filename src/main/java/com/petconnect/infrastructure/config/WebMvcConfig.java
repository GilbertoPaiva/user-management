package com.petconnect.infrastructure.config;

import com.petconnect.infrastructure.security.interceptor.SecurityInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração do Spring MVC para interceptors e outros aspectos web
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    
    private final SecurityInterceptor securityInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Registrar o SecurityInterceptor para todas as requisições
        registry.addInterceptor(securityInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/error",
                    "/favicon.ico",
                    "/h2-console/**"
                );
    }
}

package io.segmentme.core.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.core.api.error.dto.SimpleErrorDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.CharEncoding;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletResponse;

import static io.segmentme.core.api.error.dto.ErrorType.AUTHENTICATION_ERROR;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ObjectMapper objectMapper;

    private final AuthenticationManager authenticationManager;

    @Bean
    public FilterRegistrationBean<?> filterRegistrationBean() {
        var source = new UrlBasedCorsConfigurationSource();
        var config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        var bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().disable().authorizeRequests()
            .mvcMatchers("/**").authenticated()
            .and()
            .exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler())
            .authenticationEntryPoint(entryPointExceptionHandler())
            .and()
            .oauth2ResourceServer()
            .accessDeniedHandler(accessDeniedHandler())
            .authenticationEntryPoint(entryPointExceptionHandler())
            .jwt().authenticationManager(authenticationManager);
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, exception) -> writeException(response, HttpStatus.FORBIDDEN, exception);
    }

    private AuthenticationEntryPoint entryPointExceptionHandler() {
        return (request, response, exception) -> writeException(response, HttpStatus.UNAUTHORIZED, exception);
    }

    @SneakyThrows
    private void writeException(HttpServletResponse response, HttpStatus status, RuntimeException exception) {
        var out = response.getOutputStream();
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(CharEncoding.UTF_8);
        objectMapper.writeValue(out, new SimpleErrorDto(AUTHENTICATION_ERROR, exception.getMessage()));
        out.flush();
    }
}

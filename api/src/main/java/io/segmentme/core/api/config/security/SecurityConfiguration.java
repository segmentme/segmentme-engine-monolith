package io.segmentme.core.api.config.security;

import io.segmentme.core.api.config.security.dto.AuthUser;
import io.segmentme.core.api.config.security.property.SecuritySettings;
import io.segmentme.core.db.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final SecuritySettings securitySettings;

    private final UserService userService;

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new Pbkdf2PasswordEncoder(securitySettings.getPasswordSecret(), 1500, 512);
    }

    @Bean
    public FilterRegistrationBean<?> filterRegistrationBean() {
        var source = new UrlBasedCorsConfigurationSource();
        var config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/oauth/**", config);
        var bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userService.findByEmail(username)
                .map(AuthUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + username + " doesn't exist"));
    }
}

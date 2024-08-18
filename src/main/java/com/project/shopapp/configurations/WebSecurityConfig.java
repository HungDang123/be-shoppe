package com.project.shopapp.configurations;

import com.project.shopapp.filters.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> requests
                        // Allow all for registration and login
                        .requestMatchers(
                                String.format("%s/users/register", apiPrefix),
                                String.format("%s/users/login", apiPrefix),
                                String.format("%s/roles", apiPrefix),
                                String.format("%s/products/**", apiPrefix),
                                String.format("%s/categories", apiPrefix)
                        ).permitAll()

                         // POST, PUT, DELETE requests for categories
                        .requestMatchers(HttpMethod.POST, String.format("%s/categories/**", apiPrefix))
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, String.format("%s/categories/**", apiPrefix))
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, String.format("%s/categories/**", apiPrefix))
                        .hasAuthority("ROLE_ADMIN")

                        // POST, PUT, DELETE requests for products
                        .requestMatchers(HttpMethod.POST, String.format("%s/products/**", apiPrefix))
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, String.format("%s/products/**", apiPrefix))
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, String.format("%s/products/**", apiPrefix))
                        .hasAuthority("ROLE_ADMIN")

                        // POST requests for orders
                        .requestMatchers(HttpMethod.POST, String.format("%s/orders/**", apiPrefix))
                        .hasAuthority("ROLE_USER")

                        // GET requests for orders
                        .requestMatchers(HttpMethod.GET, String.format("%s/orders/**", apiPrefix))
                        .permitAll()

                        // PUT and DELETE requests for orders
                        .requestMatchers(HttpMethod.PUT, String.format("%s/orders/**", apiPrefix))
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, String.format("%s/orders/**", apiPrefix))
                        .hasAuthority("ROLE_ADMIN")

                        // All other requests
                        .anyRequest().authenticated()
                );
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173"));
                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                corsConfiguration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**",corsConfiguration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });
        return http.build();
    }
}

package com.microservice.notification.notification_service.configs;

import com.microservice.notification.notification_service.filter.JwtAuthFilter;
import com.microservice.notification.notification_service.services.UserInfoUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for Spring Security setup.
 * <p>
 * This class defines beans and configurations for securing the application,
 * including authentication providers, password encoding, and security filters.
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JwtAuthFilter authFilter;

    /**
     * Bean definition for {@link UserDetailsService}.
     * <p>
     * Provides the user details service used for authentication.
     * </p>
     *
     * @return an instance of {@link UserInfoUserDetailsService}.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        logger.info("Creating UserDetailsService bean");
        return new UserInfoUserDetailsService();
    }

    /**
     * Bean definition for {@link PasswordEncoder}.
     * <p>
     * Configures the password encoder used for encoding and validating passwords.
     * </p>
     *
     * @return an instance of {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Creating PasswordEncoder bean");
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the Spring Security filter chain.
     * <p>
     * Sets up HTTP security, authentication providers, and filters.
     * </p>
     *
     * @param http the {@link HttpSecurity} object to configure.
     * @return the configured {@link SecurityFilterChain}.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring SecurityFilterChain");
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/authenticate").permitAll()
                        .requestMatchers("/actuator/circuitbreakers").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/notification/**").authenticated())
                .sessionManagement(Customizer.withDefaults())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(Customizer.withDefaults()).disable()) // Allows H2 console within frames
                .build();
    }

    /**
     * Configures the {@link AuthenticationProvider}.
     * <p>
     * Sets up a {@link DaoAuthenticationProvider} with the user details service
     * and password encoder.
     * </p>
     *
     * @return a configured {@link AuthenticationProvider}.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        logger.info("Creating AuthenticationProvider bean");
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Configures the {@link AuthenticationManager}.
     * <p>
     * Provides the authentication manager used by Spring Security.
     * </p>
     *
     * @param config the {@link AuthenticationConfiguration} object.
     * @return the configured {@link AuthenticationManager}.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        logger.info("Creating AuthenticationManager bean");
        return config.getAuthenticationManager();
    }
}

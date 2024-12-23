package com.microservice.notification.notification_service.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for creating and managing RestTemplate beans.
 * <p>
 * This class defines a RestTemplate bean that can be used to make HTTP requests
 * in the application.
 * </p>
 */
@Configuration
public class RestTemplateConfig {

    private static final Logger logger = LoggerFactory.getLogger(RestTemplateConfig.class);

    /**
     * Creates and configures a {@link RestTemplate} bean.
     * <p>
     * This method provides a {@link RestTemplate} instance for use in making
     * REST API calls throughout the application.
     * </p>
     *
     * @return a configured {@link RestTemplate} instance.
     */
    @Bean
    public RestTemplate restTemplate() {
        logger.info("Creating RestTemplate bean");
        return new RestTemplate();
    }
}

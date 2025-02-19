package com.rewardservice.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for application-wide beans.
 */
@Configuration
public class AppConfig {

    /**
     * Creates and returns a ModelMapper bean.
     *
     * @return a new instance of ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}

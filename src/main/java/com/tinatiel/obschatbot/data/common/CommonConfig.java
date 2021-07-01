package com.tinatiel.obschatbot.data.common;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration for the common data layer components, mostly just entity scanning.
 */
@EntityScan("com.tinatiel.obschatbot.data.common")
@Configuration
public class CommonConfig {

}

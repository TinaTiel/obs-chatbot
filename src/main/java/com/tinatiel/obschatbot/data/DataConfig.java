package com.tinatiel.obschatbot.data;

import com.tinatiel.obschatbot.core.command.CommandConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Aggregates configuration for the entire data package.
 */
@Import({CommonConfig.class, CommandConfig.class})
@Configuration
public class DataConfig {

}

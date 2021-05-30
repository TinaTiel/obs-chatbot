package com.tinatiel.obschatbot.data;

import com.tinatiel.obschatbot.data.command.CommandDataConfig;
import com.tinatiel.obschatbot.data.common.CommonConfig;
import com.tinatiel.obschatbot.data.system.SystemDataConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Aggregates configuration for the entire data package.
 */
@EnableJpaRepositories
//@Import({CommonConfig.class, CommandDataConfig.class, SystemDataConfig.class})
@Configuration
public class DataConfig {

}

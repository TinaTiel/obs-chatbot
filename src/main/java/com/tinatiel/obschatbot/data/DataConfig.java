package com.tinatiel.obschatbot.data;

import com.tinatiel.obschatbot.core.command.CommandConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({CommonConfig.class, CommandConfig.class})
@Configuration
public class DataConfig {

}

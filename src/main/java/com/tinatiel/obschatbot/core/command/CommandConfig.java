/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.command;

import com.tinatiel.obschatbot.core.request.factory.expand.CommandExpanderConfig;
import com.tinatiel.obschatbot.data.command.CommandEntityService;
import com.tinatiel.obschatbot.data.command.CommandService;
import com.tinatiel.obschatbot.data.command.CommandServiceImpl;
import com.tinatiel.obschatbot.data.command.mapper.executable.ExecutableCommandMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Encapsulates all configuration for the command package.
 */
@Import(CommandExpanderConfig.class)
@Configuration
public class CommandConfig {

}

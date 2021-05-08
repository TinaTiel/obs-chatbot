/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.request.factory.CommandRequestFactoryConfig;
import com.tinatiel.obschatbot.core.request.handler.RequestHandlerConfig;
import com.tinatiel.obschatbot.core.request.messaging.RequestMessagingConfig;
import com.tinatiel.obschatbot.core.request.scheduler.RequestSchedulerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Encompasses all configuration for the request package.
 */
@Import({
  RequestHandlerConfig.class,
  CommandRequestFactoryConfig.class,
  RequestSchedulerConfig.class,
  RequestMessagingConfig.class
})
@Configuration
public class RequestConfig {

}

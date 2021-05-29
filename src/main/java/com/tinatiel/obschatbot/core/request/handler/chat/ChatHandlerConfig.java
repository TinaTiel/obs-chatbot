package com.tinatiel.obschatbot.core.request.handler.chat;

import com.tinatiel.obschatbot.data.command.CommandService;
import com.tinatiel.obschatbot.core.request.handler.CommandRequestDispatcher;
import com.tinatiel.obschatbot.core.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Contains configuration required for the ${@link ChatRequestHandlerImpl}.
 */
@Configuration
public class ChatHandlerConfig {

  @Autowired
  CommandService commandService;

  @Autowired
  CommandRequestDispatcher commandRequestDispatcher;

  @Autowired
  UserService userService;

  @Bean
  ChatRequestHandler chatRequestHandler() {
    return new ChatRequestHandlerImpl(
      chatMessageParser(),
      commandService,
      userService,
      commandRequestDispatcher
    );
  }

  @Bean
  ChatMessageParser chatMessageParser() {
    return new ChatMessageParserImpl(
      "!",
      true
    );
  }

}

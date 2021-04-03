package com.tinatiel.obschatbot.core.request.handler.chat;

import com.tinatiel.obschatbot.core.command.CommandRepository;
import com.tinatiel.obschatbot.core.request.handler.CommandRequestDispatcher;
import com.tinatiel.obschatbot.core.request.handler.RequestHandlerConfig;
import com.tinatiel.obschatbot.core.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(RequestHandlerConfig.class)
@Configuration
public class ChatHandlerConfig {

    @Autowired
    CommandRepository commandRepository;

    @Autowired
    CommandRequestDispatcher commandRequestDispatcher; // TODO: Get rid of this, it adds strange dependency patterns

    @Autowired
    UserService userService;

    @Bean
    ChatRequestHandler chatRequestHandler() {
        return new ChatRequestHandlerImpl(
                chatMessageParser(),
                commandRepository,
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

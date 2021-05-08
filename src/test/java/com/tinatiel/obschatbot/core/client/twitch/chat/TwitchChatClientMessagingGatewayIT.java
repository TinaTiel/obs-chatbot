package com.tinatiel.obschatbot.core.client.twitch.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.tinatiel.obschatbot.core.client.twitch.chat.TwitchChatClientMessagingGatewayIT.TestConfig;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import java.nio.channels.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.BridgeFrom;
import org.springframework.integration.annotation.BridgeTo;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.PollableChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {TestConfig.class, TwitchChatClientIntegrationConfig.class})
@ExtendWith(SpringExtension.class)
public class TwitchChatClientMessagingGatewayIT {

  @Autowired
  TwitchClientMessagingGateway gateway;

  @Autowired
  PollableChannel testChannel;

  @EnableIntegration
  @TestConfiguration
  public static class TestConfig {

    @BridgeFrom(value = "twitchClientLifecycleChannel")
    @Bean
    PollableChannel testChannel() {
      return new QueueChannel();
    }

    @ServiceActivator(inputChannel = "twitchClientLifecycleChannel")
    @Bean
    public MessageHandler someSubscriber() {
      return new LoggingHandler("info");
    }

  }

  @BeforeEach
  void setUp() {
    assertThat(gateway).isNotNull();
  }

  @Test
  void messageReceivedFromGateway() {

    // Given a payload
    ObsChatbotEvent payload = mock(ObsChatbotEvent.class);

    // When sent via the gateway
    gateway.submit(payload);

    // Then it is received
    Message<?> message = testChannel.receive();
    assertThat(message).isNotNull();
    assertThat(message.getPayload()).isInstanceOf(ObsChatbotEvent.class).isEqualTo(payload);

  }

}

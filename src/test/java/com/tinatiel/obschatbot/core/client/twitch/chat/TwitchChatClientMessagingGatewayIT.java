package com.tinatiel.obschatbot.core.client.twitch.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.tinatiel.obschatbot.core.SpringIntegrationTestConfig;
import com.tinatiel.obschatbot.core.client.twitch.chat.messaging.TwitchChatClientMessagingConfig;
import com.tinatiel.obschatbot.core.client.twitch.chat.messaging.TwitchClientMessagingGateway;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.channel.interceptor.WireTap;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.PollableChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@EnableIntegration
@ContextConfiguration(classes = {
  TwitchChatClientMessagingConfig.class,
  SpringIntegrationTestConfig.class,
  TwitchChatClientMessagingGatewayIT.TestConfig.class
})
@ExtendWith(SpringExtension.class)
public class TwitchChatClientMessagingGatewayIT {

  @Autowired
  TwitchClientMessagingGateway gateway;

  @Autowired
  PollableChannel testChannel;

  @Autowired
  Queue<Message<?>> testChannelQueue;

  @Qualifier("twitchClientLifecycleChannel")
  @Autowired
  AbstractMessageChannel targetChannel;

  @TestConfiguration
  public static class TestConfig {

    @ServiceActivator(inputChannel = "twitchClientLifecycleChannel")
    @Bean
    public MessageHandler someSubscriber() {
      return new LoggingHandler("info");
    }

  }

  @BeforeEach
  void setUp() {
    assertThat(gateway).isNotNull();
    assertThat(testChannelQueue).isEmpty();
    assertThat(testChannel.receive(1)).isNull();
    targetChannel.addInterceptor(new WireTap(testChannel));
  }

  @Test
  void messageReceivedFromGatewayFoo() {

    // Given a payload
    ObsChatbotEvent payload = mock(ObsChatbotEvent.class);

    // When sent via the gateway
    gateway.submit(payload);

    // Then it is received
    System.out.println("Messages: " + testChannelQueue.stream().collect(Collectors.toList()));
    Message<?> message = testChannel.receive(1);
    assertThat(message).isNotNull();
    assertThat(message.getPayload()).isInstanceOf(ObsChatbotEvent.class).isEqualTo(payload);

  }

}

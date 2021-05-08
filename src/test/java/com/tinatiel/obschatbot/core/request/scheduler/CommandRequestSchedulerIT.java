package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.SpringIntegrationTestConfig;
import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.twitch.chat.TwitchChatClientDelegate;
import com.tinatiel.obschatbot.core.client.twitch.chat.TwitchChatClientSettings;
import com.tinatiel.obschatbot.core.command.CommandRepository;
import com.tinatiel.obschatbot.core.messaging.Listener;
import com.tinatiel.obschatbot.core.request.*;
import com.tinatiel.obschatbot.core.request.messaging.CommandRequestGateway;
import com.tinatiel.obschatbot.core.user.*;
import java.util.Queue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pircbotx.PircBotX;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.channel.interceptor.WireTap;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@EnableIntegration
@ContextConfiguration(classes = {
  SpringIntegrationTestConfig.class,
  RequestConfig.class,
  CommandRequestSchedulerIT.TestConfig.class
})
@SpringJUnitConfig
class CommandRequestSchedulerIT {

  @Autowired
  QueueChannel testChannel;

  @Autowired
  Queue<Message<?>> testChannelQueue;

  @Qualifier("actionRequestChannel")
  @Autowired
  AbstractMessageChannel targetChannel;

  @Autowired
  CommandRequestGateway commandRequestGateway;

  // Mock out dependencies we don't want to use
  @MockBean
  CommandRepository commandRepository;
  @MockBean
  UserService userService;

  @TestConfiguration
  static class TestConfig {

    // required; actionRequestChannel doesn't have subs otherwise in this test
    @ServiceActivator(inputChannel = "actionRequestChannel")
    @Bean
    MessageHandler someSubscriber() {
      return new LoggingHandler("info");
    }

  }

  @BeforeEach
  void setUp() {

    // Intercept messages from the target channel
    targetChannel.addInterceptor(new WireTap(testChannel));

  }

  @AfterEach
  void tearDown() {
    // Clear the channel and remove the interceptor
    testChannel.clear();
    targetChannel.removeInterceptor(0);
  }

  @Test
  void whenRequestsAreSubmittedThenTheyEndUpInTheActionRequestQueue() {

      // Given some contexts
      RequestContext broadcasterContext = new RequestContext(
              User.builder()
                  .platform(Platform.TWITCH)
                  .username("tinatiel")
                  .userSecurityDetails(UserSecurityDetails.builder().broadcaster(true).build())
                  .build(), new ArrayList<>());
      RequestContext moderatorContext = new RequestContext(User.builder()
              .platform(Platform.TWITCH)
              .username("mango")
              .userSecurityDetails(UserSecurityDetails.builder().moderator(true).build())
              .build(), new ArrayList<>());

      RequestContext otherContext = new RequestContext(User.builder()
              .platform(Platform.TWITCH)
              .username("follower78")
              .userSecurityDetails(UserSecurityDetails.builder().following(true).build())
              .build(), new ArrayList<>());
      // Given some requests
      List<CommandRequest> requests = Arrays.asList(
              new CommandRequest(otherContext, Arrays.asList(
                      new NonblockingAction("1.1"),
                      new NonblockingAction("1.2"),
                      new NonblockingAction("1.3")
              )),
              new CommandRequest(moderatorContext, Arrays.asList(
                      new NonblockingAction("2.1"),
                      new NonblockingAction("2.2")
              )),
              new CommandRequest(otherContext, Arrays.asList(
                      new NonblockingAction("3.1"),
                      new BlockingAction("3.2"), // will automatically unblock after 1000ms, we should see 3.3 again near the end of the queue
                      new NonblockingAction("3.3"),
                      new NonblockingAction("3.4"),
                      new NonblockingAction("3.5")
              )),
              new CommandRequest(otherContext, Arrays.asList(
                      new NonblockingAction("4.1"),
                      new NonblockingAction("4.2"),
                      new NonblockingAction("4.3")
              )),
              new CommandRequest(broadcasterContext, Arrays.asList(
                      new NonblockingAction("5.1")
              )),
              new CommandRequest(otherContext, Arrays.asList(
                      new NonblockingAction("6.1"),
                      new NonblockingAction("6.2"),
                      new NonblockingAction("6.3")
              ))
      );

      // When submitted to the commandRequestQueue
      requests.forEach(commandRequestGateway::submit);

      // And we wait reasonably
      try {
          Thread.sleep(1500);
      } catch (InterruptedException e) {
          e.printStackTrace();
      }

      // Then the actionRequestQueue receives the actions (implicitly, via the scheduler)
      // (having the same size as the total number of actions in the command requests)
      System.out.println(testChannelQueue);
      assertThat(testChannelQueue).hasSize(17);

  }

}
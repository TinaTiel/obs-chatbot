package com.tinatiel.obschatbot.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.model.WaitAction;
import com.tinatiel.obschatbot.core.client.system.SystemClientConfig;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.messaging.CommandRequestGateway;
import com.tinatiel.obschatbot.core.request.messaging.RequestMessagingConfig;
import com.tinatiel.obschatbot.core.request.scheduler.RequestSchedulerConfig;
import com.tinatiel.obschatbot.core.user.User;
import com.tinatiel.obschatbot.core.user.UserSecurityDetails;
import java.time.Duration;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.channel.interceptor.WireTap;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.Message;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@EnableIntegration
@ContextConfiguration(classes = {
  SpringIntegrationTestConfig.class,
  RequestMessagingConfig.class,
  RequestSchedulerConfig.class,
  SystemClientConfig.class
})
@SpringJUnitConfig
public class CommandRequestE2eTests {

  @Autowired
  Queue<Message<?>> testChannelQueue;

  @Autowired
  QueueChannel testChannel;

  @Qualifier("actionRequestChannel")
  @Autowired
  AbstractMessageChannel targetChannel;

  @Autowired
  CommandRequestGateway commandRequestGateway;

  @BeforeEach
  void setUp() {
    // Intercept messages from the lifecycle channel
    targetChannel.addInterceptor(new WireTap(testChannel));
  }

  @AfterEach
  void tearDown() {
    // Clear the channel and remove the interceptor
    testChannel.clear();
    targetChannel.removeInterceptor(0);
  }

  /**
   * Test that everything is wired up correctly across different contexts, as the waits are released
   * ultimately on workgroups within the scheduler, and delegation to work groups is based on
   * the request context.
   */
  @Timeout(5)
  @ParameterizedTest
  @MethodSource("requestContexts")
  void waitActionResolesInExpectedTime(RequestContext context) throws InterruptedException {

    // Given a Wait request, and one command after (doesn't matter what)
    long waitMs = 2000;
    long timeoutMs = 10000; // long timeout
    WaitAction waitAction = new WaitAction(Duration.ofMillis(waitMs), Duration.ofMillis(timeoutMs));
    Action someAction = mock(Action.class);

    // When submitted for execution
    commandRequestGateway.submit(new CommandRequest(context, Arrays.asList(
      waitAction, someAction
    )));

    // And we wait a reasonable amount of time
    Thread.sleep(waitMs + 500);

    // Then the next command will have been sent for execution
    assertThat(testChannelQueue).hasSize(2);

  }

  static Stream<Arguments> requestContexts() {
    return Stream.of(
      Arguments.of(
        new RequestContext(User.builder()
        .username("broadcaster").userSecurityDetails(UserSecurityDetails.builder()
          .broadcaster(true).build()
        ).build(), new ArrayList<>())
      ),
      Arguments.of(new RequestContext(User.builder()
          .username("moderator").userSecurityDetails(UserSecurityDetails.builder()
            .moderator(true).build()
          ).build(), new ArrayList<>())
      ),
      Arguments.of(new RequestContext(User.builder()
          .username("subscriber").userSecurityDetails(UserSecurityDetails.builder()
            .patron(true).patronPeriod(Period.ofMonths(3)).build()
          ).build(), new ArrayList<>())
      ),
      Arguments.of(new RequestContext(User.builder()
          .username("follower").userSecurityDetails(UserSecurityDetails.builder()
            .following(true).build()
          ).build(), new ArrayList<>())
      ),
      Arguments.of(new RequestContext(User.builder()
          .username("guest").userSecurityDetails(UserSecurityDetails.builder().build()
          ).build(), new ArrayList<>())
      )

    );
  }

}

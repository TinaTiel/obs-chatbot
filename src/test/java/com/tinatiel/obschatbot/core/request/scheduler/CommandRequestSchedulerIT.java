package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.command.CommandConfig;
import com.tinatiel.obschatbot.core.messaging.QueueNotifier;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import com.tinatiel.obschatbot.core.request.RequestConfig;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.core.user.User;
import com.tinatiel.obschatbot.core.user.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {RequestConfig.class, CommandConfig.class})
@SpringJUnitConfig
class CommandRequestSchedulerIT {

    @Autowired
    BlockingQueue<CommandRequest> commandRequestQueue;

    @Autowired
    BlockingQueue<ActionRequest> actionRequestQueue;

    // mock any QueueNotifier consuming ActionRequests so we can
    // examine the queue afterwards ourselves.
    @MockBean
    QueueNotifier<ActionRequest> disabledActionRequestQueueNotifier;

    @Test
    void whenRequestsAreSubmittedThenTheyEndUpInTheActionRequestQueue() {

        // Given some contexts
        RequestContext broadcasterContext = new RequestContext(new User(Platform.TWITCH, "tinatiel", UserType.BROADCASTER), new ArrayList<>());
        RequestContext moderatorContext = new RequestContext(new User(Platform.TWITCH, "mango", UserType.MODERATOR), new ArrayList<>());
        RequestContext otherContext = new RequestContext(new User(Platform.TWITCH, "other", UserType.FOLLOWER), new ArrayList<>());

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
        commandRequestQueue.addAll(requests);

        // And we wait reasonably
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Then the actionRequestQueue receives the actions (implicitly, via the scheduler)
        // (having the same size as the total number of actions in the command requests)
        System.out.println(actionRequestQueue);
        assertThat(actionRequestQueue).hasSize(17);

    }

}
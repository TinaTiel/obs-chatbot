package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.messaging.Listener;
import com.tinatiel.obschatbot.core.request.*;
import com.tinatiel.obschatbot.core.user.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;

//@ContextConfiguration(classes = {QueueNotifierConfig.class})
@SpringJUnitConfig
class CommandRequestSchedulerIT {

    @Autowired
    BlockingQueue<CommandRequest> commandRequestQueue;

    @Autowired
    BlockingQueue<ActionRequest> actionRequestQueue;

    // MOck out stuff we don't need
    @MockBean
    Listener<ActionRequest> twitchChatActionRequestListener;

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
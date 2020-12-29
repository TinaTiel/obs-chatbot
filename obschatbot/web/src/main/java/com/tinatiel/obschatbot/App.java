package com.tinatiel.obschatbot;

import com.tinatiel.obschatbot.core.action.enumerator.ActionEnumerator;
import com.tinatiel.obschatbot.core.actionservice.Platform;
import com.tinatiel.obschatbot.core.actionservice.chat.twitch.TwitchChatClient;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.command.CommandRepository;
import com.tinatiel.obschatbot.core.dispatch.CommandRequest;
import com.tinatiel.obschatbot.core.user.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(App.class);
//        ObsClient client = context.getBean(ObsClient.class);
//        client.connect();
//        client.setSourceVisibility("Scene", "someTextSource", false);
//
//        ActionServiceFactory factory = context.getBean(ActionServiceFactory.class);
//
//        Action action = new ObsSourceVisibilityAction(
//                new ActionContext("", "", new ArrayList<>()), factory,
//                "Scene",
//                "someTextSource",
//                false
//        );
//        action.run();

        CommandRepository commandRepository = context.getBean(CommandRepository.class);
        ActionEnumerator actionEnumerator = context.getBean(ActionEnumerator.class);
        User user = new User(Platform.TWITCH, "test");
        commandRepository.findByName("foo")
                .ifPresent(cmd -> {
                    actionEnumerator.enumerate(cmd,
                            new CommandRequest(user, cmd, Arrays.asList("arg1", "arg2", "arg3"))).stream()
                            .forEach(action -> {
                                action.run();
                            });
                });

//        System.out.println("You should see this message in the logs");
//        TwitchChatClient client = context.getBean(TwitchChatClient.class);
//        try {
//            Thread.sleep(3000); // wait for connection to establish
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        client.sendMessage("FOOOOO");

    }
}

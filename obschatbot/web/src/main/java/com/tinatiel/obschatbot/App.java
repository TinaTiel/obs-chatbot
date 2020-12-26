package com.tinatiel.obschatbot;

import com.tinatiel.obschatbot.core.actionservice.chat.twitch.TwitchChatClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

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

        System.out.println("You should see this message in the logs");
        TwitchChatClient client = context.getBean(TwitchChatClient.class);
        try {
            Thread.sleep(3000); // wait for connection to establish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.sendMessage("FOOOOO");

    }
}

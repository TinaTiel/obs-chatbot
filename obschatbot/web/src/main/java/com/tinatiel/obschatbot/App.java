package com.tinatiel.obschatbot;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.ActionContext;
import com.tinatiel.obschatbot.core.action.impl.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.actionservice.ActionServiceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(App.class);
//        ObsClient client = context.getBean(ObsClient.class);
//        client.setSourceVisibility("Scene", "someTextSource", false);

        ActionServiceFactory factory = context.getBean(ActionServiceFactory.class);

        Action action = new ObsSourceVisibilityAction(
                new ActionContext("", "", new ArrayList<>()), factory,
                "Scene",
                "someTextSource",
                false
        );
        action.run();

    }
}

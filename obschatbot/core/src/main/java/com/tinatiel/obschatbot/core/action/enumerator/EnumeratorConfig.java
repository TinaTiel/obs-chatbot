package com.tinatiel.obschatbot.core.action.enumerator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnumeratorConfig {

    @Bean
    public ActionEnumerator actionEnumerator() {
        return new ActionEnumeratorImpl();
    }

}

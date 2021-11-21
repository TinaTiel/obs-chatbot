package com.tinatiel.obschatbot.commandservice.args;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {ActionsArgsProcessorFactoryConfig.class})
@SpringJUnitConfig
public class ActionArgsProcessorFactoryIT {

  @Autowired ActionArgsProcessorFactory sut;

  @Test
  void beanIsConfigured() {
    // Just test that the bean exists
  }
}

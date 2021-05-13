package com.tinatiel.obschatbot.data.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.tinatiel.obschatbot.data.CommonConfig;
import com.tinatiel.obschatbot.data.command.entity.CommandEntityRepository;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {CommonConfig.class, CommandDataConfig.class})
@DataJpaTest
public class CommandEntityServiceTest {

  @Autowired
  CommandEntityService service;

  @Autowired
  CommandEntityRepository repository;

  @BeforeEach
  void setUp() {
    repository.deleteAll();
  }

  @Test
  void createAndRetrieveNewCommand() {

    // Given a command
    CommandDto request = CommandDto.builder()
      .name("foo")
      .build();

    // When saved
    CommandDto result = service.save(request);

    // Then it can be retrieved
    assertThat(result.getId()).isNotNull();
    Optional<CommandDto> byId = service.findById(result.getId());
    Optional<CommandDto> byName = service.findByName(result.getName());

    assertThat(byId).isPresent();
    assertThat(byName).isPresent();
    assertThat(result).usingRecursiveComparison().isEqualTo(byName.get()).isEqualTo(byId.get());
    assertThat(result.getName()).isEqualTo(request.getName());
    assertThat(result.isDisabled()).isFalse();

  }

//  @Test
//  void disableCommand() {
//
//  }
//
//  @Test
//  void renameCommand() {
//
//  }

}

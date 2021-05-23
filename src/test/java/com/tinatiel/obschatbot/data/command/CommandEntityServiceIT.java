package com.tinatiel.obschatbot.data.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.tinatiel.obschatbot.data.command.entity.CommandEntityRepository;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerRepository;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.data.command.model.action.ActionRepository;
import com.tinatiel.obschatbot.data.command.model.action.ObsSourceVisibilityActionDto;
import com.tinatiel.obschatbot.data.command.model.action.SendMessageActionDto;
import com.tinatiel.obschatbot.data.command.model.action.WaitActionDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.InOrderSequencerDto;
import java.time.Duration;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CommandEntityServiceIT {

  @Autowired
  CommandEntityService service;
  @Autowired
  CommandEntityRepository commandEntityRepository;
  @Autowired
  SequencerRepository sequencerRepository;
  @Autowired
  ActionRepository actionRepository;

  @Test
  void saveRetrieveUpdateRetrieveDelete() {

    // Given a command
    CommandDto request = CommandDto.builder()
      .name("somecommandwitheverything")
      .sequencer(InOrderSequencerDto.builder().build())
      .actions(Arrays.asList(
        ObsSourceVisibilityActionDto.builder().position(2).sourceName("donate").visible(true).build(),
        WaitActionDto.builder().position(3).waitDuration(Duration.ofSeconds(2)).build(),
        ObsSourceVisibilityActionDto.builder().position(4).sourceName("donate").visible(false).build()
      ))
      .build();

    // When saved
    CommandDto result = service.save(request);

    // Then it can be retrieved
    CommandDto found = service.findById(result.getId()).get();

    // And when updated
    CommandDto updateRequest = found;
    updateRequest.setActions(Arrays.asList(
      SendMessageActionDto.builder().position(1).message("donate!").build(),
      result.getActions().get(0),
      result.getActions().get(1),
      result.getActions().get(2)
    ));

    CommandDto updateResult = service.save(updateRequest);

    // Then it can be retrieved again
    CommandDto foundAgain = service.findById(updateResult.getId()).get();
    assertThat(foundAgain).isNotNull();

    // And finally deleted
    service.delete(foundAgain.getId());
    assertThat(commandEntityRepository.count()).isZero();
    assertThat(actionRepository.count()).isZero();
    assertThat(sequencerRepository.count()).isZero();

  }

}

package com.tinatiel.obschatbot.web.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.data.command.model.action.ExecuteCommandActionDto;
import com.tinatiel.obschatbot.data.command.model.action.ObsSourceVisibilityActionDto;
import com.tinatiel.obschatbot.data.command.model.action.SendMessageActionDto;
import com.tinatiel.obschatbot.data.command.model.action.WaitActionDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.InOrderSequencerDto;
import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@JsonTest
public class CommandModelTest {

  @Autowired
  JacksonTester<CommandDto> jacksonTester;

  CommandDto expected = CommandDto.builder()
    .owner(UUID.fromString("7feab8dd-ceed-4927-bbac-4e9e36222149"))
    .id(UUID.fromString("e9569a8d-fbfa-4d1d-996e-88b023814014"))
    .name("somecommand")
    .sequencer(InOrderSequencerDto.builder().build())
    .disabled(true)
    .actions(Arrays.asList(
      ExecuteCommandActionDto.builder().id(UUID.fromString("71562753-dfdf-48af-8502-0722a06ba971")).position(1)
        .target(UUID.fromString("ef393dae-dcda-11eb-ba80-0242ac130004"))
        .build(),
      ObsSourceVisibilityActionDto.builder().id(UUID.fromString("aadde3c0-dcb8-11eb-ba80-0242ac130004")).position(2)
        .sceneName("somescene")
        .sourceName("somesource")
        .visible(true)
        .build(),
      SendMessageActionDto.builder().id(UUID.fromString("a196e3c0-dcb8-11eb-ba80-0242ac130004")).position(3)
        .message("howdy")
        .build(),
      WaitActionDto.builder().id(UUID.fromString("b0282a5c-dcb8-11eb-ba80-0242ac130004")).position(4)
        .waitDuration(Duration.ofSeconds(3))
        .build()
    ))
    .build();

  @Test
  void serialize() throws Exception {

    JsonContent<CommandDto> json = jacksonTester.write(expected);
    assertThat(json).isEqualToJson("command.json", JSONCompareMode.STRICT);

  }

  @Test
  void deserialize() throws Exception {
    CommandDto obj = jacksonTester.readObject("command.json");
    assertThat(obj).usingRecursiveComparison().isEqualTo(expected);
  }
}

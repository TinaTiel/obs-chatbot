package com.tinatiel.obschatbot.data.command.model;

import com.tinatiel.obschatbot.data.command.model.action.ActionDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.SequencerDto;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Provides a user-presentable representation of a Command.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommandDto {

  private UUID id;
  private String name;
  private boolean disabled;
  private SequencerDto sequencer;
  @Builder.Default
  private List<ActionDto> actions = new ArrayList<>();

}

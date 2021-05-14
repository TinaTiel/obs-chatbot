package com.tinatiel.obschatbot.data.command.model;

import com.tinatiel.obschatbot.data.command.model.sequencer.SequencerDto;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Provides a user-presentable representation of a Command.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CommandDto {

  private UUID id;
  private String name;
  private boolean disabled;
  private SequencerDto sequencer;

}

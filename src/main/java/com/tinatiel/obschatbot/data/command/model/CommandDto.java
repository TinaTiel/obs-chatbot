package com.tinatiel.obschatbot.data.command.model;

import com.tinatiel.obschatbot.data.command.model.action.ActionDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.SequencerDto;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Provides a user-presentable representation of a Command.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommandDto {

  private UUID owner;
  @NotNull
  private UUID id;
  @NotBlank
  private String name;
  private boolean disabled;
  @NotNull
  private SequencerDto sequencer;
  @Builder.Default
  private List<ActionDto> actions = new ArrayList<>();

}

package com.tinatiel.obschatbot.data.command.model.sequencer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RandomOrderSequencerDto extends SequencerDto {
  private int pickedPerExecution;
}

package com.tinatiel.obschatbot.data.command.model.sequencer;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class RandomOrderSequencerDto extends SequencerDto {
  @Positive
  private int pickedPerExecution;
}

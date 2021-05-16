package com.tinatiel.obschatbot.data.command.model.sequencer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class InOrderSequencerDto extends SequencerDto {
  private boolean reversed;
}
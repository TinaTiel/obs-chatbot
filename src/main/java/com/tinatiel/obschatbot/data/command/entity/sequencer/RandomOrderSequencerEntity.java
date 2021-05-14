package com.tinatiel.obschatbot.data.command.entity.sequencer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RandomOrderSequencerEntity extends SequencerEntity {
  private int pickedPerExecution;
}

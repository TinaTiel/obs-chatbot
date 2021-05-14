package com.tinatiel.obschatbot.data.command.entity.sequencer;

import com.tinatiel.obschatbot.data.command.model.sequencer.SequencerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InOrderSequencerEntity extends SequencerEntity {
  private boolean reversed;
}

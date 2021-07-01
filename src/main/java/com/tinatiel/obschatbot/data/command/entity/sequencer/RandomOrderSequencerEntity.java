package com.tinatiel.obschatbot.data.command.entity.sequencer;

import com.tinatiel.obschatbot.data.common.SequencerType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue(SequencerType.RANDOM_ORDER)
public class RandomOrderSequencerEntity extends SequencerEntity {
  private int pickedPerExecution;
}

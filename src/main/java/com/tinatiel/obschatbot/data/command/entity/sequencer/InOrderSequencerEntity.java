package com.tinatiel.obschatbot.data.command.entity.sequencer;

import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerEntity.Type;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue(Type.ORDERED)
public class InOrderSequencerEntity extends SequencerEntity {
  private boolean reversed;
}

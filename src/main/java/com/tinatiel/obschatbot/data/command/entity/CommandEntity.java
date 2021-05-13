package com.tinatiel.obschatbot.data.command.entity;

import com.tinatiel.obschatbot.data.common.BaseEntity;
import javax.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class CommandEntity extends BaseEntity {
  private String name;
//  private SequencerEntity sequencer;
  private boolean disabled;
//  private List<ActionEntity> actions = new ArrayList<>();

}

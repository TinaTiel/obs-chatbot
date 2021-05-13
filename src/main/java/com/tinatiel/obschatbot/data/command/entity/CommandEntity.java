package com.tinatiel.obschatbot.data.command.entity;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.data.BaseEntity;
import com.tinatiel.obschatbot.data.command.entity.action.ActionEntity;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;

@Entity
public class CommandEntity extends BaseEntity {
  private String name;
//  private SequencerEntity sequencer;
  private boolean disabled;
//  private List<ActionEntity> actions = new ArrayList<>();
}

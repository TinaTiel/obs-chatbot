package com.tinatiel.obschatbot.data.command.entity;

import com.tinatiel.obschatbot.data.command.entity.action.ActionEntity;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerEntity;
import com.tinatiel.obschatbot.data.common.BaseEntity;
import com.tinatiel.obschatbot.data.common.OwnedEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Persistable Command.
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = "command", uniqueConstraints = {@UniqueConstraint(columnNames = {"owner", "name"})})
@Entity
public class CommandEntity extends OwnedEntity {

  @NotBlank
  @Column(nullable = false)
  private String name;

  @OneToOne(
    cascade = {CascadeType.ALL},
    orphanRemoval = true
  )
  @JoinColumn(name = "sequencer_id")
  private SequencerEntity sequencer;

  private boolean disabled;

  @OneToMany(
    cascade = {CascadeType.ALL},
    orphanRemoval = true
  )
  @JoinColumn(name = "command_id")
  private List<ActionEntity> actions = new ArrayList<>();

  public void setActions(List<ActionEntity> newActions) {
    this.actions.clear();
    for(ActionEntity action:newActions) {
      addAction(action);
    }
  }

  public void addAction(ActionEntity action) {
//    action.setPosition(this.actions.size());
    this.actions.add(action);
  }

  public void removeAction(ActionEntity action) {
    this.actions.remove(action);
  }

}

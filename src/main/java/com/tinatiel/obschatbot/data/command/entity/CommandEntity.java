package com.tinatiel.obschatbot.data.command.entity;

import com.tinatiel.obschatbot.data.command.entity.action.ActionEntity;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerEntity;
import com.tinatiel.obschatbot.data.common.IdEntity;
import com.tinatiel.obschatbot.data.common.OwnerEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

/**
 * Persistable Command.
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = "command", uniqueConstraints = {@UniqueConstraint(columnNames = {"owner", "name"})})
@Entity
public class CommandEntity extends IdEntity {

  @Type(type = "org.hibernate.type.UUIDCharType")
  @Column(length = 36, columnDefinition = "varchar", updatable = false, nullable = false)
  private UUID owner;

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

  @OrderBy("position")
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

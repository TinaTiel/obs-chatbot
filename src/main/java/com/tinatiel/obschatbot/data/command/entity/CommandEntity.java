package com.tinatiel.obschatbot.data.command.entity;

import com.tinatiel.obschatbot.data.common.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

/**
 * Persistable Command.
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = "command")
@Entity
public class CommandEntity extends BaseEntity {

  @NotBlank
  @NaturalId
  @Column(nullable = false, unique = true)
  private String name;
  //  private SequencerEntity sequencer;
  private boolean disabled;
  //  private List<ActionEntity> actions = new ArrayList<>();

}

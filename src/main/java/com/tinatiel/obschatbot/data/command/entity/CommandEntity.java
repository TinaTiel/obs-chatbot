package com.tinatiel.obschatbot.data.command.entity;

import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerEntity;
import com.tinatiel.obschatbot.data.common.BaseEntity;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
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
  @Column(nullable = false, unique = true)
  private String name;

//  @PrimaryKeyJoinColumn
//  @OneToOne(mappedBy = "command", cascade = CascadeType.ALL)
  @Transient
  private SequencerEntity sequencer;

  private boolean disabled;
  //  private List<ActionEntity> actions = new ArrayList<>();

}

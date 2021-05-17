package com.tinatiel.obschatbot.data.command.entity;

import com.tinatiel.obschatbot.data.command.entity.action.ActionEntity;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerEntity;
import com.tinatiel.obschatbot.data.common.BaseEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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

  @OneToOne(mappedBy = "command",
    cascade = CascadeType.ALL,
    fetch = FetchType.EAGER, // We will always need the sequencer
    optional = false)
  private SequencerEntity sequencer;

  private boolean disabled;

  @OneToMany(
    cascade = {CascadeType.ALL},
    orphanRemoval = true
  )
  @JoinColumn(name = "command_id")
  private List<ActionEntity> actions = new ArrayList<>();

}

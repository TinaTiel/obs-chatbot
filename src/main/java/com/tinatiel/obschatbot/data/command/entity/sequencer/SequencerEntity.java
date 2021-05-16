package com.tinatiel.obschatbot.data.command.entity.sequencer;

import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

/**
 * Represents a persistable sequencer configuration / instance.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sequencer")
public class SequencerEntity {

  public static final class Type {
    public static final String ORDERED = "ORDERED";
    public static final String RANDOM_ORDER = "RANDOM_ORDER";
  }

  @Id
  @org.hibernate.annotations.Type(type = "org.hibernate.type.UUIDCharType")
  @Column(name = "command_id", length = 36, columnDefinition = "varchar", updatable = false, nullable = false)
  protected UUID id;

  @MapsId
  @ManyToOne(fetch = FetchType.EAGER)
  protected CommandEntity command;

  private String sequencerType;

  // InOrder
  private boolean reversed;

  // RandomOrder
  private int pickedPerExecution;

}

package com.tinatiel.obschatbot.data.command.entity.sequencer;

import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a persistable sequencer configuration / instance.
 */
@Getter
@Setter
@NoArgsConstructor
//@Entity
public class SequencerEntity {

//  @Column(name = "command_id")
//  @Id
  private UUID id;

//  @MapsId
//  @OneToOne
//  @JoinColumn(name = "command_id")
  private CommandEntity command;

}

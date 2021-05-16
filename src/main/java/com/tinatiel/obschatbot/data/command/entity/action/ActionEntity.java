package com.tinatiel.obschatbot.data.command.entity.action;

import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a persistable Action.
 *
 * @see com.tinatiel.obschatbot.core.action.Action
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class ActionEntity {
  //  @Id
//  @org.hibernate.annotations.Type(type = "org.hibernate.type.UUIDCharType")
//  @Column(name = "command_id", length = 36, columnDefinition = "varchar", updatable = false, nullable = false)
  protected UUID id;

  //  @MapsId
//  @ManyToOne(fetch = FetchType.EAGER)
  protected CommandEntity command;

  //  @NotNull(message = "Position is required")
//  @Column(nullable = false)
  private Integer position;

}

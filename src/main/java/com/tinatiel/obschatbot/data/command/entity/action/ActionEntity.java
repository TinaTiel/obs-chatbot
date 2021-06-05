package com.tinatiel.obschatbot.data.command.entity.action;

import com.tinatiel.obschatbot.data.common.IdEntity;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
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
@Entity
@Table(name = "action")
@Inheritance(strategy =  InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "action_type", discriminatorType = DiscriminatorType.STRING)
public abstract class ActionEntity extends IdEntity {

  @Column(nullable = false)
  private Integer position;

}

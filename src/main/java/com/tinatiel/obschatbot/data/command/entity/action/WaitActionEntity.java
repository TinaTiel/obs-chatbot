package com.tinatiel.obschatbot.data.command.entity.action;

import com.tinatiel.obschatbot.data.common.ActionType;
import java.time.Duration;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An Action that introduces an artificial wait/delay during an action sequence, for example
 * adding a wait when showing a media source in OBS before hiding it again.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue(ActionType.WAIT)
public class WaitActionEntity extends ActionEntity {

  private Duration waitDuration;

}

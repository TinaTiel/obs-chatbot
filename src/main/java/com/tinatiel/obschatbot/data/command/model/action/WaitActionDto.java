package com.tinatiel.obschatbot.data.command.model.action;

import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * An Action that introduces an artificial wait/delay during an action sequence, for example
 * adding a wait when showing a media source in OBS before hiding it again.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class WaitActionDto extends ActionDto {

  private Duration waitDuration;
  
}

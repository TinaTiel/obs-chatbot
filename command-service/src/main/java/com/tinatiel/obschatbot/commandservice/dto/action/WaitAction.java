package com.tinatiel.obschatbot.commandservice.dto.action;

import com.tinatiel.obschatbot.commandservice.dto.CommandArgs;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * An Action that introduces an artificial wait/delay during an action sequence, for example
 * adding a wait when showing a media source in OBS before hiding it again.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class WaitAction implements Action {
  private Duration waitDuration;
  private Duration waitTimeout;

  @Override
  public Action withCommandArgs(CommandArgs commandArgs) {
    return this;
  }
}

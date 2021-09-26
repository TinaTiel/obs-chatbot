package com.tinatiel.obschatbot.data.command.mapper.executable;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.NoOpAction;
import com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.action.model.WaitAction;
import com.tinatiel.obschatbot.data.command.model.action.ActionDto;
import com.tinatiel.obschatbot.data.command.model.action.ObsSourceVisibilityActionDto;
import com.tinatiel.obschatbot.data.command.model.action.SendMessageActionDto;
import com.tinatiel.obschatbot.data.command.model.action.WaitActionDto;

public class ExecutableActionMapperImpl implements ExecutableActionMapper {

  @Override
  public Action map(ActionDto dto) {

    // Default to NoOp action rather than null, to be more robust downstream
    if (dto == null)
      return new NoOpAction();

    // Otherwise map
    if (dto instanceof ObsSourceVisibilityActionDto) {
      ObsSourceVisibilityActionDto castDto = (ObsSourceVisibilityActionDto) dto;
      return new ObsSourceVisibilityAction(
        castDto.getSceneName(),
        castDto.getSourceName(),
        castDto.isVisible()
      );
    } else if (dto instanceof SendMessageActionDto) {
      return new SendMessageAction(((SendMessageActionDto) dto).getMessage());
    } else if (dto instanceof WaitActionDto) {
      return new WaitAction(((WaitActionDto) dto).getWaitDuration());
    }

    else {
      throw new RuntimeException("No mapping for " + dto.getClass());
    }

  }

}

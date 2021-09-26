package com.tinatiel.obschatbot.data.command.mapper;

import com.tinatiel.obschatbot.core.error.UnexpectedException;
import com.tinatiel.obschatbot.data.command.entity.action.ActionEntity;
import com.tinatiel.obschatbot.data.command.entity.action.ExecuteCommandActionEntity;
import com.tinatiel.obschatbot.data.command.entity.action.ObsSourceVisibilityActionEntity;
import com.tinatiel.obschatbot.data.command.entity.action.SendMessageActionEntity;
import com.tinatiel.obschatbot.data.command.entity.action.WaitActionEntity;
import com.tinatiel.obschatbot.data.command.model.action.ActionDto;
import com.tinatiel.obschatbot.data.command.model.action.ExecuteCommandActionDto;
import com.tinatiel.obschatbot.data.command.model.action.ObsSourceVisibilityActionDto;
import com.tinatiel.obschatbot.data.command.model.action.SendMessageActionDto;
import com.tinatiel.obschatbot.data.command.model.action.WaitActionDto;
import java.util.UUID;
import org.mapstruct.Mapper;

@Mapper(imports = UUID.class)
public interface ActionMapper {

  default ActionEntity map(ActionDto dto) {
    if(dto == null) return null;
    if(dto instanceof ExecuteCommandActionDto) {
      return this.map((ExecuteCommandActionDto) dto);
    } else if (dto instanceof  ObsSourceVisibilityActionDto) {
      return this.map((ObsSourceVisibilityActionDto) dto);
    } else if (dto instanceof SendMessageActionDto) {
      return this.map((SendMessageActionDto) dto);
    } else if (dto instanceof WaitActionDto) {
      return this.map((WaitActionDto) dto);
    } else {
      throw new UnexpectedException("Could not map action " + dto);
    }
  }

  default ActionDto map(ActionEntity entity) {
    if(entity == null) return null;
    if(entity instanceof ExecuteCommandActionEntity) {
      return this.map((ExecuteCommandActionEntity) entity);
    } else if (entity instanceof ObsSourceVisibilityActionEntity) {
      return this.map((ObsSourceVisibilityActionEntity) entity);
    } else if (entity instanceof SendMessageActionEntity) {
      return this.map((SendMessageActionEntity) entity);
    } else if (entity instanceof WaitActionEntity) {
      return this.map((WaitActionEntity) entity);
    } else {
      throw new UnexpectedException("Could not map action " + entity);
    }
  }

  ExecuteCommandActionEntity map(ExecuteCommandActionDto dto);
  ExecuteCommandActionDto map(ExecuteCommandActionEntity entity);

  ObsSourceVisibilityActionEntity map(ObsSourceVisibilityActionDto dto);
  ObsSourceVisibilityActionDto map(ObsSourceVisibilityActionEntity entity);

  SendMessageActionEntity map(SendMessageActionDto dto);
  SendMessageActionDto map(SendMessageActionEntity entity);

  WaitActionEntity map(WaitActionDto dto);
  WaitActionDto map(WaitActionEntity entity);

}

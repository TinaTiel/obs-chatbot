package com.tinatiel.obschatbot.data.command.model.action;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonTypeInfo(use= Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="@type")
@JsonSubTypes({
  @Type(value = ExecuteCommandActionDto.class, name = "sys.execute-command"),
  @Type(value = ObsSourceVisibilityActionDto.class, name = "obs.source-visibility"),
  @Type(value = SendMessageActionDto.class, name = "twitch.send-message"),
  @Type(value = WaitActionDto.class, name = "sys.wait"),
})
public abstract class ActionDto {

  protected UUID id;
  protected Integer position;

}

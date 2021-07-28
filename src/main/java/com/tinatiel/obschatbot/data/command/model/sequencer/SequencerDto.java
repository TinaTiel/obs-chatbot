package com.tinatiel.obschatbot.data.command.model.sequencer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(use= Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="@type")
@JsonSubTypes({
  @Type(value = InOrderSequencerDto.class, name = "ordered"),
  @Type(value = RandomOrderSequencerDto.class, name = "random")
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public abstract class SequencerDto {
  @NotNull
  private UUID id;
}

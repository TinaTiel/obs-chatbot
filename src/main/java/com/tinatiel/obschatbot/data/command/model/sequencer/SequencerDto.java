package com.tinatiel.obschatbot.data.command.model.sequencer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use= Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="@type")
@JsonSubTypes({
  @Type(value = InOrderSequencerDto.class, name = "ordered"),
  @Type(value = RandomOrderSequencerDto.class, name = "random")
})
public abstract class SequencerDto {

}

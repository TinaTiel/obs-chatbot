package com.tinatiel.obschatbot.data.command.model.action;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class ActionDto {

  protected Integer position;

}

package com.tinatiel.obschatbot.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ClientStatusRequestDto {
  public static enum State { START, STOP, RESTART }
  private State state;
}

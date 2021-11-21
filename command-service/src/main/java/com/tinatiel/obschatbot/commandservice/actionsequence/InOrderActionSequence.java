package com.tinatiel.obschatbot.commandservice.actionsequence;

import com.tinatiel.obschatbot.commandservice.action.Action;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InOrderActionSequence implements ActionSequence {
  private List<Action> actions;
  private boolean reverse;
}

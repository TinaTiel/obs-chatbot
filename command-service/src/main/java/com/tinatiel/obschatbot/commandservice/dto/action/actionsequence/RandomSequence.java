package com.tinatiel.obschatbot.commandservice.dto.action.actionsequence;

import com.tinatiel.obschatbot.commandservice.dto.action.Action;
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
public class RandomSequence implements ActionSequence {
  private List<Action> actions;
  private int pickQty;
  private boolean maintainOrder;
}

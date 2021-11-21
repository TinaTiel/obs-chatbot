package com.tinatiel.obschatbot.commandservice.actionsequence;

import com.tinatiel.obschatbot.commandservice.action.Action;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomSequenceGenerator implements ActionSequenceGenerator<RandomSequence> {

  private final Random random = new Random();

  @Override
  public List<Action> generate(RandomSequence actionSequence) {

    // Determine the pick qty; if negative, zero, or greater than
    // available in the sequence, then match the sequence qty
    int pickQty = actionSequence.getPickQty();
    int seqSize = actionSequence.getActions().size();
    if(pickQty <= 0 || pickQty > seqSize) {
        pickQty = seqSize;
    }

    // Generate a list of indexes to pick from
    List<Integer> indexes = IntStream.range(0, seqSize).boxed().collect(Collectors.toList());

    // Pick some indexes at random
    List<Integer> pickedIndexes = new ArrayList<>();
    while(pickedIndexes.size() < pickQty) {
      pickedIndexes.add(indexes.remove(random.nextInt(indexes.size())));
    }

    // If maintaining order, then sort it
    if(actionSequence.isMaintainOrder()) {
      Collections.sort(pickedIndexes);
    }

    // Return the results
    List<Action> results = new ArrayList<>();
    pickedIndexes.forEach(index -> {
      results.add(actionSequence.getActions().get(index));
    });
    return results;

  }

}

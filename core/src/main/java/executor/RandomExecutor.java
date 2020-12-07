package executor;

import action.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomExecutor implements Executor {

    private List<Action> available = new ArrayList<>();
    private List<Action> executed = new ArrayList<>();
    private final Integer pickedPerExecution;
    private final Random random = new Random();

    public RandomExecutor(List<Action> actions, Integer pickedPerExecution) {

        // Sanitize
        if(actions == null) {
            throw new IllegalArgumentException("actions cannot be null");
        }

        // Initialize the pool of actions to pick from
        available.addAll(actions);

        // Determine how many to pick from the pool each execution
        if(pickedPerExecution == null || pickedPerExecution > actions.size()) {
            this.pickedPerExecution = actions.size();
        } else {
            this.pickedPerExecution = pickedPerExecution;
        }

    }

    @Override
    public void execute(List<String> arguments) {

        // sanitize inputs
        if(arguments == null) {
            throw new IllegalArgumentException("arguments cannot be null");
        }

        for(int i=0; i<pickedPerExecution; i++) {

            // If there's nothing available, then repopulate the list
            if(available.size() <= 0) {
                available.addAll(executed);
                executed.clear();
            }

            // Pick an action from what's available to execute, and execute it
            Action picked = available.remove(random.nextInt(available.size()));
            picked.execute(arguments);

            // Add the executed action to the list of executed actions
            executed.add(picked);
        }

    }

    @Override
    public List<Action> getActions() {
        return Stream.concat(available.stream(), executed.stream()).collect(Collectors.toList());
    }
}

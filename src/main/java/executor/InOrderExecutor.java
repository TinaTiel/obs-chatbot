package executor;

import action.Action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InOrderExecutor implements Executor {

    private final boolean reverse;
    private final List<Action> actions = new ArrayList<>();

    public InOrderExecutor(List<Action> actions, boolean reverse) {
        if(actions == null) throw new IllegalArgumentException("actions cannot be null");
        this.actions.addAll(actions);
        this.reverse = reverse;
    }

    @Override
    public void execute(List<String> arguments) {
        List<Action> actionsToExecute = new ArrayList<>(actions);
        if(reverse) Collections.reverse(actionsToExecute);
        for(Action action:actionsToExecute) {
            action.execute(arguments);
        }
    }

    @Override
    public List<Action> getActions() {
        return new ArrayList<>(actions);
    }
}

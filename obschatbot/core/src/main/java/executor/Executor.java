package executor;

import action.Action;

import java.util.List;

public interface Executor {
    void execute(List<String> arguments);
    List<Action> getActions();
}

import java.util.ArrayList;
import java.util.List;

public class Command {

    private final List<Action> actions = new ArrayList<>();
    private final Executor executor;

    public Command(Executor executor) {
        this.executor = executor;
    }

    public void execute(List<String> arguments) {
        executor.execute(actions, arguments);
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public List<Action> getActions() {
        return new ArrayList<>(actions);
    }
}

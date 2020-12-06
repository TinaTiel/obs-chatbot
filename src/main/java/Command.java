import java.util.ArrayList;
import java.util.List;

public class Command {

    private List<Action> actions = new ArrayList<>();

    public void execute(List<String> arguments) {
        for(Action action:actions) {
            action.execute(arguments);
        }
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public List<Action> getActions() {
        return actions;
    }
}

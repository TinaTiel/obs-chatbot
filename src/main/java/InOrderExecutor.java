import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InOrderExecutor implements Executor {

    private final boolean reverse;

    public InOrderExecutor(boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public void execute(List<Action> actions, List<String> arguments) {
        List<Action> actionsToExecute = new ArrayList<>(actions);
        if(reverse) Collections.reverse(actionsToExecute);
        for(Action action:actionsToExecute) {
            action.execute(arguments);
        }
    }
}

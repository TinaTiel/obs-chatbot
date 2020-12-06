import java.util.List;

public class InOrderExecutor implements Executor {

    @Override
    public void execute(List<Action> actions, List<String> arguments) {
        for(Action action:actions) {
            action.execute(arguments);
        }
    }
}

import java.util.List;

public interface Executor {
    void execute(List<Action> actions, List<String> arguments);
}

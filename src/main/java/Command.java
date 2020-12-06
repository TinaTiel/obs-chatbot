import executor.Executor;

import java.util.List;

public class Command {

    private final Executor executor;

    public Command(Executor executor) {
        this.executor = executor;
    }

    public void execute(List<String> arguments) {
        executor.execute(arguments);
    }

    public Executor getExecutor() {
        return executor;
    }

}

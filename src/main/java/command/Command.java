package command;

import executor.Executor;

import java.util.List;

public class Command {

    private final String name;
    private final Executor executor;
    private final boolean disabled;

    public Command(String name, Executor executor, boolean disabled) {
        this.name = name;
        this.executor = executor;
        this.disabled = disabled;
    }

    public void execute(List<String> arguments) {
        executor.execute(arguments);
    }

    public Executor getExecutor() {
        return executor;
    }

}

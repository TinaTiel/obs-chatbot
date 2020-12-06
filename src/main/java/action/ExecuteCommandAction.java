package action;

import command.Command;

import java.util.List;

public class ExecuteCommandAction implements Action {

    private final Command command;

    public ExecuteCommandAction(Command command) {
        this.command = command;
    }

    @Override
    public void execute(List<String> arguments) {
        command.execute(arguments);
    }
}

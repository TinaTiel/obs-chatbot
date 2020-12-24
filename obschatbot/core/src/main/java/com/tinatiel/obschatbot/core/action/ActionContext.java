package com.tinatiel.obschatbot.core.action;

import java.util.List;
import java.util.Objects;

public class ActionContext {

    private final String user;
    private final String command;
    private final List<String> arguments;

    public ActionContext(String user, String command, List<String> arguments) {
        this.user = user;
        this.command = command;
        this.arguments = arguments;
    }

    public String getCommand() {
        return command;
    }

    public String getUser() {
        return user;
    }

    public List<String> getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionContext context = (ActionContext) o;
        return Objects.equals(user, context.user) && Objects.equals(command, context.command) && Objects.equals(arguments, context.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, command, arguments);
    }

    @Override
    public String toString() {
        return "ActionContext{" +
                "command='" + command + '\'' +
                ", user='" + user + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}

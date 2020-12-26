package com.tinatiel.obschatbot.core.dispatch.chat;

import java.util.List;

public class ChatMessageParseResult {

    private final String commandName;
    private final List<String> args;

    public ChatMessageParseResult(String commandName, List<String> args) {
        this.commandName = commandName;
        this.args = args;
    }

    public String getCommandName() {
        return commandName;
    }

    public List<String> getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return "ChatMessageParseResult{" +
                "commandName='" + commandName + '\'' +
                ", args=" + args +
                '}';
    }
}

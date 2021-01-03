/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.command;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.RunnableAction;
import com.tinatiel.obschatbot.core.sequencer.ActionSequencer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Command {

    private String name;
    private ActionSequencer actionSequencer;
    private boolean disabled = false;

    public Command() {}

    public Command name(String name) {
        this.name = name;
        return this;
    }

    public Command actionSequencer(ActionSequencer actionSequencer) {
        this.actionSequencer = actionSequencer;
        return this;
    }

    public Command disabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public String getName() {
        return name;
    }

    public ActionSequencer getActionSequencer() {
        return actionSequencer;
    }

    public List<Action> getActions() {
        return new ArrayList<>(actionSequencer.listAll());
    }

    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return Objects.equals(name, command.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
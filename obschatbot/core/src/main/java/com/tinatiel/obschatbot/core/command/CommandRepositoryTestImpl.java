/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.command;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.impl.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.action.sequencer.ActionSequencer;
import com.tinatiel.obschatbot.core.action.sequencer.InOrderActionSequencer;
import com.tinatiel.obschatbot.core.actionservice.ActionServiceFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandRepositoryTestImpl implements CommandRepository {

    private final ActionServiceFactory factory;

    private List<Action> actions = new ArrayList<>();
    private ActionSequencer actionSequencer;
    private Command command;

    public CommandRepositoryTestImpl(ActionServiceFactory factory) {
        this.factory = factory;

        actions.add(
                new ObsSourceVisibilityAction(
                        null, factory,
                        "Scene",
                        "someTextSource",
                        false
                )
        );
        actionSequencer = new InOrderActionSequencer(actions, false);
        command = new Command().name("foo").actionSequencer(actionSequencer);
    }

    @Override
    public Optional<Command> findByName(String name) {
        if(name.equals(command.getName())) {
            return Optional.of(command);
        } else {
            return Optional.empty();
        }
    }

}

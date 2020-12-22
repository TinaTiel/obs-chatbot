package com.tinatiel.obschatbot.core.actionschedule;

import com.tinatiel.obschatbot.core.action.Action;

import java.util.List;

public interface ActionSequencer {

    List<Action> nextSequence();

}

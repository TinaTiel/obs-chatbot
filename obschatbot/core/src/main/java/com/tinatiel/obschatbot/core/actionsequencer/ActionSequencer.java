package com.tinatiel.obschatbot.core.actionsequencer;

import com.tinatiel.obschatbot.core.action.Action;

import java.util.List;

/**
 * Determines the sequence of actions that will be scheduled at a later point (e.g. by an Executor/queue)
 *
 * Note that Actions aren't an input; this is because the ActionSequencer is stateful. For example, it may
 * need to know what the previous sequence was to ensure all actions are sequenced eventually (such as a randomSequencer
 * where X of Y actions are sequenced each call, but it's cyclical/pseudorandom to ensure all actions eventually
 * are sequenced).
 *
 * Note also this is not called a Scheduler, as that would imply the actions would be executed at some point in
 * time. The ActionSequencer is only responsible for determining the order of execution; scheduling should be
 * delegated to an Executor to scheduled the actions be executed in order (but at different times depending on the
 * time it takes to execute an Action and when the OS schedules the Thread to execute it).
 */
public interface ActionSequencer {

    List<Action> nextSequence();
    List<Action> getActions();

}

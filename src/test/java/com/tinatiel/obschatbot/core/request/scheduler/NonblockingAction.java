package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.action.Action;

class NonblockingAction implements Action<NonblockingAction> {

    private final String name;

    public NonblockingAction(String name) {
        this.name = name;
    }

    @Override
    public NonblockingAction clone() {
        return null; // ignore
    }

    @Override
    public boolean requiresCompletion() {
        return false;
    }

    @Override
    public long getTimeout() {
        return 0;
    }

    @Override
    public String toString() {
        return "NonblockingAction{" +
                "name='" + name + '\'' +
                '}';
    }
}

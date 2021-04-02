package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.action.Action;

class BlockingAction implements Action<BlockingAction> {

    private final String name;

    public BlockingAction(String name) {
        this.name = name;
    }

    @Override
    public BlockingAction clone() {
        return null; // ignore
    }

    @Override
    public boolean requiresCompletion() {
        return true;
    }

    @Override
    public long getTimeout() {
        return 1000;
    }

    @Override
    public String toString() {
        return "BlockingAction{" +
                "name='" + name + '\'' +
                '}';
    }
}

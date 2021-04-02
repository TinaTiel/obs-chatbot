package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

import java.util.UUID;

public class ActionCompleteEvent extends AbstractObsChatbotEvent {

    private final UUID completedActionRequestId;

    public ActionCompleteEvent(UUID completedActionRequestId) {
        super();
        this.completedActionRequestId = completedActionRequestId;
    }

    public UUID getCompletedActionRequestId() {
        return completedActionRequestId;
    }

    @Override
    public String toString() {
        return "ActionCompleteEvent{" +
                super.toString() +
                "completedActionRequestId=" + completedActionRequestId +
                '}';
    }
}

package com.tinatiel.obsclient.model.response;

public abstract class AbstractObsResponse implements ObsResponse {

    private final String messageId;

    private final Status status;

    private final String error;

    protected AbstractObsResponse(String messageId, Status status, String error) {
        this.messageId = messageId;
        this.status = status;
        this.error = error;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public String getError() {
        return error;
    }

}

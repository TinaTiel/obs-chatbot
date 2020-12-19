package com.tinatiel.obsclient.model.response;

public class Response {

    private final String messageId;
    private final Status status;
    private final String error;

    public Response(String messageId, Status status, String error) {
        this.messageId = messageId;
        this.status = status;
        this.error = error;
    }

    public String getMessageId() {
        return messageId;
    }

    public Status getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

}

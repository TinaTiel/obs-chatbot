package com.tinatiel.obsclient.model.request;

public abstract class AbstractRequest {

    private final String requestType;
    private final String messageId;

    protected AbstractRequest(String requestType, String messageId) {
        this.requestType = requestType;
        this.messageId = messageId;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getMessageId() {
        return messageId;
    }

    @Override
    public String toString() {
        return "AbstractRequest{" +
                "requestType='" + requestType + '\'' +
                ", messageId='" + messageId + '\'' +
                '}';
    }

}

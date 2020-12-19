package com.tinatiel.obsclient.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public abstract class AbstractRequest {

    @JsonProperty("request-type")
    private final String requestType;

    @JsonProperty("message-id")
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

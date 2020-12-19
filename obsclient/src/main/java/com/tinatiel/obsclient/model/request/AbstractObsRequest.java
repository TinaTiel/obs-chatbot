package com.tinatiel.obsclient.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public abstract class AbstractObsRequest implements ObsRequest {

    @JsonProperty("request-type")
    protected final String requestType;

    @JsonProperty("message-id")
    protected final String messageId;

    protected AbstractObsRequest(String requestType, String messageId) {
        this.requestType = requestType;
        this.messageId = messageId;
    }

    @Override
    public String getRequestType() {
        return requestType;
    }

    @Override
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

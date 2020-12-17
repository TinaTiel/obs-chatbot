package com.tinatiel.obsclient.example.spring;

public class GetVersion {

    String requestType;
    String messageId;

    public GetVersion(String messageId) {
        this.requestType = "GetVersion";
        this.messageId = messageId;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getMessageId() {
        return messageId;
    }
}

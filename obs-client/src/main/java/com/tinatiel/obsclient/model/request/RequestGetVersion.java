package com.tinatiel.obsclient.model.request;

public class RequestGetVersion extends AbstractRequest {

    public RequestGetVersion(String messageId) {
        super("GetVersion", messageId);
    }

    @Override
    public String toString() {
        return "RequestGetVersion{" +
                "requestType='" + requestType + '\'' +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}

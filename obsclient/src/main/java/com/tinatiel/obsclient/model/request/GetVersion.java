package com.tinatiel.obsclient.model.request;

public class GetVersion extends AbstractObsRequest {

    public GetVersion(String messageId) {
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

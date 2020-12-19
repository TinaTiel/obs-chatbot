package com.tinatiel.obsclient.model.request;

public class GetVersionRequest extends AbstractObsRequest {

    public GetVersionRequest(String messageId) {
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

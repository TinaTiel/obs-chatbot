package com.tinatiel.obsclient.model.response;

public interface ObsResponse {
    String getMessageId();
    Status getStatus();
    String getError();
}

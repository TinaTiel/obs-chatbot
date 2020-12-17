package com.tinatiel.obsclient.example.spring;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class GetVersionEncoder implements Encoder.Text<GetVersion> {

    @Override
    public String encode(GetVersion getVersion) throws EncodeException {
        return "{\"request-type\": \"GetVersion\",\"message-id\":\"" + getVersion.getMessageId() + "\"}";
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}

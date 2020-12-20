package com.tinatiel.obsclient.model.response;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class ObsResponseDecoder implements Decoder.Text<ObsResponse> {

    ObjectMapper objectMapper;

    public ObsResponseDecoder() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ObsResponse decode(String s) throws DecodeException {
        return null;
    }

    @Override
    public boolean willDecode(String s) {
        return false;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}

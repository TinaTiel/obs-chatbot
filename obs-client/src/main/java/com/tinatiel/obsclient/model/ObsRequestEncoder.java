package com.tinatiel.obsclient.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinatiel.obsclient.model.request.RequestGetVersion;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class ObsRequestEncoder implements Encoder.Text<RequestGetVersion> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String encode(RequestGetVersion object) throws EncodeException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new EncodeException(object, e.getMessage());
        }
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}

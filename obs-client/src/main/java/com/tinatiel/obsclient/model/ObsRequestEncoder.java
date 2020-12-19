package com.tinatiel.obsclient.model;

import com.tinatiel.obsclient.model.request.RequestGetVersion;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class ObsRequestEncoder implements Encoder.Text<RequestGetVersion> {

    @Override
    public String encode(RequestGetVersion requestGetVersion) throws EncodeException {
        return null;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}

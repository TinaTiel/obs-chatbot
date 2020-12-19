package com.tinatiel.obsclient.encode;

import com.tinatiel.obsclient.model.ObsRequestEncoder;
import com.tinatiel.obsclient.model.request.RequestGetVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.websocket.Encoder;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class EncoderTest {

    Encoder.Text encoder;

    @BeforeEach
    void setUp() {
        encoder = new ObsRequestEncoder();
    }

    @Test
    void encodeAsExpected() throws Exception {

        // Given a request
        RequestGetVersion request = new RequestGetVersion("1");

        // When encoded
        String result = encoder.encode(request);

        // Then it is encoded as expected
        String expected = "{\"request-type\": \"GetVersion\", \"message-id\": \"1\"}";
        assertThat(result).isEqualTo(expected);

    }

}

package com.tinatiel.obsclient;

import com.tinatiel.obsclient.model.ObsRequestEncoder;
import com.tinatiel.obsclient.model.request.GetVersionRequest;
import com.tinatiel.obsclient.model.request.SetSceneItemRenderRequest;
import com.tinatiel.obsclient.model.response.GetVersionResponse;
import com.tinatiel.obsclient.model.response.ObsResponseDecoder;
import com.tinatiel.obsclient.model.response.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.websocket.Decoder;
import javax.websocket.Encoder;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class EncodeDecodeTest {

    Encoder.Text encoder;
    Decoder.Text decoder;

    @BeforeEach
    void setUp() {
        encoder = new ObsRequestEncoder();
        decoder = new ObsResponseDecoder();
    }

    @ParameterizedTest()
    @MethodSource("encoderArguments")
    void encodeAsExpected(Object request, String expectedJson) throws Exception {

        // When encoded
        String actual = encoder.encode(request);
        System.out.println("Encoded object : " + request);
        System.out.println("To Json        : " + actual);

        // Then it is encoded as expected (ignoring json field order, as it doesn't matter)
        JSONAssert.assertEquals(expectedJson, actual, false);

    }

    @ParameterizedTest()
    @MethodSource("decoderArguments")
    void decodeAsExpected(String inputJson, Object expectedObject) throws Exception {

        // When decoded
        Object actual = decoder.decode(inputJson);
        System.out.println("Decoded Json : " + inputJson);
        System.out.println("To Object    : " + actual);

        // Then it is decoded as expected
        assertThat(actual).isEqualToComparingFieldByField(expectedObject);

    }

    static Stream<Arguments> encoderArguments() {
        return Stream.of(
            Arguments.of(
                    new GetVersionRequest("1"),
                    "{\"request-type\": \"GetVersion\", \"message-id\": \"1\"}"
            ),
            Arguments.of(
                    new SetSceneItemRenderRequest("1", "foo", "bar", true),
                    "{\"request-type\": \"SetSceneItemRender\", \"message-id\": \"1\", \"scene-name\": \"foo\", \"source\": \"bar\", \"render\": true}"
            )
        );
    }

    static Stream<Arguments> decoderArguments() {
        return Stream.of(
                Arguments.of(
                        "{\"message-id\": \"1\", \"status\": \"ok\", \"version\": 1.0, \"obs-websocket-version\": \"foo\", \"obs-studio-version\": \"bar\", \"available-requests\": \"a,b,c\", \"supported-image-export-formats\": \"d,e,f\" }",
                        new GetVersionResponse("1", 1.0d, "foo", "bar", Arrays.asList("a", "b", "c"), Arrays.asList("d", "e", "f"))
                )
        );
    }

}

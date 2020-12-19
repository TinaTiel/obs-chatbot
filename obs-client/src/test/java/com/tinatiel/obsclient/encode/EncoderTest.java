package com.tinatiel.obsclient.encode;

import com.tinatiel.obsclient.model.ObsRequestEncoder;
import com.tinatiel.obsclient.model.request.GetVersion;
import com.tinatiel.obsclient.model.request.SetSceneItemRender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.websocket.Encoder;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class EncoderTest {

    Encoder.Text encoder;

    @BeforeEach
    void setUp() {
        encoder = new ObsRequestEncoder();
    }

    @ParameterizedTest()
    @MethodSource("encoderArguments")
    void encodeAsExpected(Object request, String expectedJson) throws Exception {

        // When encoded
        String actual = encoder.encode(request);

        // Then it is encoded as expected (ignoring json field order, as it doesn't matter)
        JSONAssert.assertEquals(expectedJson, actual, false);

    }

    static Stream<Arguments> encoderArguments() {
        return Stream.of(
            Arguments.of(
                    new GetVersion("1"),
                    "{\"request-type\": \"GetVersion\", \"message-id\": \"1\"}"
            ),
            Arguments.of(
                    new SetSceneItemRender("1", "foo", "bar", true),
                    "{\"request-type\": \"SetSceneItemRender\", \"message-id\": \"1\", \"scene-name\": \"foo\", \"source\": \"bar\", \"render\": true}"
            )
        );
    }

}

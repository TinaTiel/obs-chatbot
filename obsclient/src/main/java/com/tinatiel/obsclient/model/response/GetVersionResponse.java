package com.tinatiel.obsclient.model.response;

import java.util.ArrayList;
import java.util.List;

public class GetVersionResponse extends AbstractObsResponse {

    private final Double version;
    private final String obsWebsocketVersion;
    private final String obsStudioVersion;
    private final List<String> availableRequestTypes = new ArrayList<>();
    private final List<String> supportedImageExportFormats = new ArrayList<>();

    public GetVersionResponse(String messageId,
                              Double version,
                              String obsWebsocketVersion,
                              String obsStudioVersion,
                              List<String> availableRequestTypes,
                              List<String> supportedImageExportFormats) {
        super(messageId, Status.OK, null);
        this.version = version;
        this.obsWebsocketVersion = obsWebsocketVersion;
        this.obsStudioVersion = obsStudioVersion;
        this.availableRequestTypes.addAll(availableRequestTypes);
        this.supportedImageExportFormats.addAll(supportedImageExportFormats);
    }

    public Double getVersion() {
        return version;
    }

    public String getObsWebsocketVersion() {
        return obsWebsocketVersion;
    }

    public String getObsStudioVersion() {
        return obsStudioVersion;
    }

    public List<String> getAvailableRequestTypes() {
        return availableRequestTypes;
    }

    public List<String> getSupportedImageExportFormats() {
        return supportedImageExportFormats;
    }
}

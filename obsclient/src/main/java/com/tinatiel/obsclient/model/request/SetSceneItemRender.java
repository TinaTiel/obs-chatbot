package com.tinatiel.obsclient.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SetSceneItemRender extends AbstractObsRequest {

    @JsonProperty("scene-name")
    private final String sceneName;
    private final String source;
    private final boolean render;

    public SetSceneItemRender( String messageId, String sceneName, String source, boolean render) {
        super("SetSceneItemRender", messageId);
        this.sceneName = sceneName;
        this.source = source;
        this.render = render;
    }

    public String getSceneName() {
        return sceneName;
    }

    public String getSource() {
        return source;
    }

    public boolean isRender() {
        return render;
    }

    @Override
    public String toString() {
        return "SetSceneItemRender{" +
                "requestType='" + requestType + '\'' +
                ", messageId='" + messageId + '\'' +
                ", sceneName='" + sceneName + '\'' +
                ", source='" + source + '\'' +
                ", render=" + render +
                '}';
    }
}

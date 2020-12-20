package com.tinatiel.obschatbot.core.action.obs;

public interface ObsClient {

    void connect();
    void setSourceVisibility(String scene, String source, boolean visibility);

}

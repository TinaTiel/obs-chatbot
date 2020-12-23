package com.tinatiel.obschatbot.core.actionservice.obs;

import com.tinatiel.obschatbot.core.actionservice.ActionService;

public interface ObsClient extends ActionService {

    void connect();
    void setSourceVisibility(String scene, String source, boolean visibility);

}

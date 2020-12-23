package com.tinatiel.obschatbot.core.actionservice.obs;

import net.twasi.obsremotejava.OBSRemoteController;

public class ObsClientImpl implements ObsClient {

    private final OBSRemoteController controller;

    public ObsClientImpl(OBSRemoteController obsRemoteController) {
        this.controller = obsRemoteController;
    }

    @Override
    public void connect() {
        if(controller.isFailed()) {
            System.out.println("Failed to connect");
        }
    }

    @Override
    public void setSourceVisibility(String scene, String source, boolean visibility) {
        controller.setSourceVisibility(scene, source, visibility, (it) -> {
            if(it.getError() == null || !it.getError().isEmpty()) {
                System.out.println("Error: " + it.getError());
            }
            System.out.println("[id=" + it.getMessageId() + "] Status: " + it.getStatus());
        });
    }
}

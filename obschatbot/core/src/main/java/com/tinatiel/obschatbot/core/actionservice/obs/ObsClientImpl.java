package com.tinatiel.obschatbot.core.actionservice.obs;

import com.tinatiel.obschatbot.core.error.ServiceNotReadyException;
import net.twasi.obsremotejava.OBSRemoteController;

import javax.annotation.PostConstruct;

public class ObsClientImpl implements ObsClient {

    private final OBSRemoteController controller;

    public ObsClientImpl(OBSRemoteController obsRemoteController) {
        this.controller = obsRemoteController;
    }

    @PostConstruct
    public void init() {
        connect(); // this is a blocking call
    }

    @Override
    public void connect() {
        try {
            controller.connect();
        } catch (Exception e) {
            throw new ServiceNotReadyException("OBS Client failed to start", null);
        }
        if(controller.isFailed()) {
            throw new ServiceNotReadyException("OBS Client failed to start", null);
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

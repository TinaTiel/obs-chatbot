/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.action.ActionType;
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

    @Override
    public ActionType getActionType() {
        return ActionType.OBS;
    }
}

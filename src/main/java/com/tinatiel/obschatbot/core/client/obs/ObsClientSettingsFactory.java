/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;

public class ObsClientSettingsFactory implements ClientSettingsFactory<ObsSettings> {

    private final ObsSettings settings;

    public ObsClientSettingsFactory(ObsSettings settings) {
        this.settings = settings;
    }

    @Override
    public ObsSettings getSettings() {
        return settings;
    }
}

/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.request.queue.consumers.ActionCommandConsumer;

public interface ObsClientManager extends ActionCommandConsumer {
    void start();
    void stop();
    void reload();
    ObsClient getClient();
}

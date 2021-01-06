/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.queue.consumers;

import com.tinatiel.obschatbot.core.request.queue.ActionCommand;

public interface ActionCommandConsumer extends Runnable {
    void consume(ActionCommand actionCommand);
}

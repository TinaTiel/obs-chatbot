/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.client.ActionCommandConsumer;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import net.twasi.obsremotejava.OBSRemoteController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deprecated.
 */
public class ObsActionCommandConsumer implements ActionCommandConsumer {

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  private final OBSRemoteController client;

  public ObsActionCommandConsumer(OBSRemoteController client) {
    this.client = client;
  }

  @Override
  public void consume(ActionRequest actionRequest) {
//        try {
//            if(actionRequest.getAction() instanceof ObsSourceVisibilityAction) {
//                ObsSourceVisibilityAction action = (ObsSourceVisibilityAction) actionRequest.getAction();
//                client.setSourceVisibility(action.getSceneName(), action.getSourceName(), action.isVisible(), (result) -> {
//                    log.debug("Executed " + actionRequest + " with result " + result);
//                    actionRequest.complete(null);
//                });
//            }
//        } finally {
//            actionRequest.cancel(true);
//        }
  }
}

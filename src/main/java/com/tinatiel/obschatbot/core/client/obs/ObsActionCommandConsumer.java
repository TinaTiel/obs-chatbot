/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.client.ActionCommandConsumer;
import com.tinatiel.obschatbot.core.client.event.ClientErrorEvent;
import com.tinatiel.obschatbot.core.client.obs.messaging.ObsClientLifecycleGateway;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import net.twasi.obsremotejava.OBSRemoteController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deprecated.
 */
public class ObsActionCommandConsumer implements ActionCommandConsumer<ObsClientDelegate> {

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  private final ObsClientLifecycleGateway lifecycleGateway;

  public ObsActionCommandConsumer(
    ObsClientLifecycleGateway lifecycleGateway) {
    this.lifecycleGateway = lifecycleGateway;
  }

  @Override
  public void consume(ObsClientDelegate client, ActionRequest actionRequest) {
      try {
          if(actionRequest.getAction() instanceof ObsSourceVisibilityAction) {
              ObsSourceVisibilityAction action = (ObsSourceVisibilityAction) actionRequest.getAction();
              client.getClient().setSourceVisibility(
                action.getSceneName(), action.getSourceName(), action.isVisible(), (result) -> {});
          }
      } catch (Exception e) {
        lifecycleGateway.submit(new ClientErrorEvent("Could not execute " + actionRequest, e));
      }
  }
}

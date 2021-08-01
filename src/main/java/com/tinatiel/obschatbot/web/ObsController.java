package com.tinatiel.obschatbot.web;

import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.data.client.obs.ObsClientDataService;
import com.tinatiel.obschatbot.data.client.obs.model.ObsClientSettingsDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import com.tinatiel.obschatbot.web.model.ClientStatusRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = WebConfig.BASE_PATH + "/obs")
@RestController
@RequiredArgsConstructor
public class ObsController {

  private final OwnerService ownerService;
  private final ObsClientDataService obsClientDataService;
  private final ClientManager obsClientManager;

  @GetMapping("/settings")
  ResponseEntity<ObsClientSettingsDto> getOBsSettings() {
    return obsClientDataService.findByOwner(ownerService.getOwner().getId())
      .map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/settings")
  ResponseEntity<Void> saveObsSettings(@RequestBody ObsClientSettingsDto settingsDto) {
    settingsDto.setOwner(ownerService.getOwner().getId());
    obsClientDataService.save(settingsDto);
    return ResponseEntity.ok(null);
  }

  @PutMapping("/status")
  ResponseEntity<Void> saveObsStatus(@RequestBody ClientStatusRequestDto statusRequest) {
    if(statusRequest != null) {
      switch (statusRequest.getState()) {
        case START:
          obsClientManager.startClient();
          break;
        case STOP:
          obsClientManager.stopClient();
          break;
        case RESTART:
          obsClientManager.reloadClient();
          break;
      }
    }
    return ResponseEntity.ok(null);
  }

}

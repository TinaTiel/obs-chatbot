package com.tinatiel.obschatbot.web;

import com.tinatiel.obschatbot.data.system.SystemSettingsDataService;
import com.tinatiel.obschatbot.data.system.model.SystemSettingsDto;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = WebConfig.BASE_PATH + "/settings")
@RestController
@RequiredArgsConstructor
public class SettingsController {

  private final OwnerService ownerService;
  private final SystemSettingsDataService systemSettingsDataService;

  private static final String SYSTEM = "/system";

  @GetMapping(SYSTEM)
  ResponseEntity<SystemSettingsDto> getSystemSettings() {
    return systemSettingsDataService.findByOwner(ownerService.getOwner().getId())
      .map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping(SYSTEM)
  ResponseEntity<Void> saveSystemSettings(@RequestBody SystemSettingsDto systemSettingsDto) {
    systemSettingsDto.setOwner(ownerService.getOwner().getId());
    systemSettingsDataService.save(systemSettingsDto);
    return ResponseEntity.ok(null);
  }

}

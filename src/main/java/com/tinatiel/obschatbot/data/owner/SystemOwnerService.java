package com.tinatiel.obschatbot.data.owner;

import java.util.UUID;

public class SystemOwnerService implements OwnerService {

  public static final UUID SYSTEM_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
  public static final String SYSTEM_PRINCIPAL = "SYSTEM";

  @Override
  public OwnerDto getOwner() {
    return OwnerDto.builder().id(SYSTEM_ID).name(SYSTEM_PRINCIPAL).build();
  }
}

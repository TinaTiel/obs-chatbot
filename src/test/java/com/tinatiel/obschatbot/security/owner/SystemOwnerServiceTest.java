package com.tinatiel.obschatbot.security.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class SystemOwnerServiceTest {

  OwnerService ownerService = new SystemOwnerService();

  @Test
  void returnsSameOwnerEveryInvocation() {

    // Given an expected system owner
    OwnerDto expected = OwnerDto.builder()
      .id(UUID.fromString("00000000-0000-0000-0000-00000000000"))
      .name("SYSTEM")
      .build();

    // When called many times
    List<OwnerDto> results = new ArrayList<>();
    for(int i = 0; i < 100; i++) {
      results.add(ownerService.getOwner());
    }

    // Then the result matches expected everytime (no random UUID etc)
    for(OwnerDto result:results) {
      assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

  }

}

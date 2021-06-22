package com.tinatiel.obschatbot.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.tinatiel.obschatbot.data.client.obs.ObsClientDataService;
import com.tinatiel.obschatbot.data.client.obs.entity.ObsClientDataRepository;
import com.tinatiel.obschatbot.data.client.twitch.auth.TwitchClientAuthDataService;
import com.tinatiel.obschatbot.data.client.twitch.auth.entity.TwitchClientAuthDataRepository;
import com.tinatiel.obschatbot.data.client.twitch.chat.TwitchClientChatDataService;
import com.tinatiel.obschatbot.data.client.twitch.chat.entity.TwitchClientChatDataRepository;
import com.tinatiel.obschatbot.data.system.SystemSettingsDataService;
import com.tinatiel.obschatbot.data.system.entity.SystemSettingsRepository;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import com.tinatiel.obschatbot.security.owner.SystemOwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

/**
 * This test verifies initial data loaded into an empty database
 */
// Re-start the container with in-memory db to guarantee it is empty when started
@DirtiesContext
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:testdb"})
@SpringBootTest
public class DefaultDataE2eIT {

  // Load SUTs
  @Autowired OwnerService ownerService;
  @Autowired ObsClientDataService obsClientDataService;
  @Autowired TwitchClientChatDataService twitchClientChatDataService;
  @Autowired TwitchClientAuthDataService twitchClientAuthDataService;
  @Autowired SystemSettingsDataService systemSettingsDataService;

  @Test
  void dataWasLoaded() {

    // Owner is default owner
    OwnerDto ownerDto = ownerService.getOwner();
    assertThat(ownerDto).isNotNull();
    assertThat(ownerDto.getId()).isEqualTo(SystemOwnerService.SYSTEM_ID);
    assertThat(ownerDto.getName()).isEqualTo(SystemOwnerService.SYSTEM_PRINCIPAL);

    // And settings are retrievable (we verify what they are in separate tests)
    assertThat(obsClientDataService.findByOwner(ownerDto.getId())).isPresent();
//    assertThat(twitchClientChatDataService.findByOwner(ownerDto.getId())).isPresent();
//    assertThat(twitchClientAuthDataService.findByOwner(ownerDto.getId())).isPresent();
//    assertThat(systemSettingsDataService.findByOwner(ownerDto.getId())).isPresent();

  }

}

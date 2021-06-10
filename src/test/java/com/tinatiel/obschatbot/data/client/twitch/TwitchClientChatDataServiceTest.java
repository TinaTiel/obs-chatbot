package com.tinatiel.obschatbot.data.client.twitch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tinatiel.obschatbot.data.client.twitch.auth.model.TwitchClientAuthDataDto;
import com.tinatiel.obschatbot.data.client.twitch.chat.TwitchClientChatDataConfig;
import com.tinatiel.obschatbot.data.client.twitch.chat.TwitchClientChatDataService;
import com.tinatiel.obschatbot.data.client.twitch.chat.entity.TwitchClientChatDataEntity;
import com.tinatiel.obschatbot.data.client.twitch.chat.entity.TwitchClientChatDataRepository;
import com.tinatiel.obschatbot.data.client.twitch.chat.model.TwitchClientChatDataDto;
import com.tinatiel.obschatbot.data.common.CommonConfig;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
  CommonConfig.class,
  TwitchClientChatDataConfig.class,
  TwitchClientChatDataServiceTest.TestConfig.class})
@DataJpaTest
public class TwitchClientChatDataServiceTest {

  @EnableJpaRepositories(basePackages = "com.tinatiel.obschatbot.data.client.twitch.chat")
  @Configuration
  static class TestConfig {

  }

  @Autowired
  TwitchClientChatDataRepository twitchClientChatDataRepository;

  @Autowired
  TwitchClientChatDataService twitchClientChatDataService;

  TwitchClientChatDataEntity existingTwitchClientData;

  @BeforeEach
  void setUp() {
    twitchClientChatDataRepository.deleteAll();
    TwitchClientChatDataEntity existing = new TwitchClientChatDataEntity();
    existing.setOwner(UUID.randomUUID());
    existing.setBotAccountUsername("mrdata");
    existing.setBroadcasterChannelUsername("fleetstar");
    existing.setConnectionAttempts(42);
    existing.setConnectionTimeoutMs(6969);
    existing.setTrigger("$");
    existing.setParseEntireMessage(true);
    existing.setJoinMessage("🖖 Welcome all 🖖");
    existing.setLeaveMessage("Live long and prosper");

    existingTwitchClientData = twitchClientChatDataRepository.saveAndFlush(existing);
    assertThat(existingTwitchClientData.getOwner()).isNotNull();
  }

  @Test
  void getExistingSettings() {

    // Given settings exist
    assertThat(twitchClientChatDataRepository.count()).isEqualTo(1);

    // When called
    Optional<TwitchClientChatDataDto> actual = twitchClientChatDataService.findByOwner(
      existingTwitchClientData.getOwner());

    // Then the expected DTO is returned
    assertThat(actual).isPresent();
    TwitchClientChatDataDto expected = TwitchClientChatDataDto.builder()
      .owner(existingTwitchClientData.getOwner())
      .botAccountUsername(existingTwitchClientData.getBotAccountUsername())
      .broadcasterChannelUsername(existingTwitchClientData.getBroadcasterChannelUsername())
      .connectionAttempts(existingTwitchClientData.getConnectionAttempts())
      .connectionTimeoutMs(existingTwitchClientData.getConnectionTimeoutMs())
      .trigger(existingTwitchClientData.getTrigger())
      .parseEntireMessage(existingTwitchClientData.isParseEntireMessage())
      .joinMessage(existingTwitchClientData.getJoinMessage())
      .leaveMessage(existingTwitchClientData.getLeaveMessage())
      .build();
    assertThat(actual.get()).usingRecursiveComparison().isEqualTo(expected);

  }

  @Test
  void saveRetrieveNewSettings() {

    // Given a request for new settings
    TwitchClientChatDataDto request = TwitchClientChatDataDto.builder()
      .owner(UUID.randomUUID())
      .botAccountUsername("mrroboto")
      .broadcasterChannelUsername("80sbeast")
      .connectionAttempts(67)
      .connectionTimeoutMs(9999)
      .trigger("🌠")
      .parseEntireMessage(true)
      .joinMessage("🎸 Rockin' my dudes 🎸")
      .leaveMessage("Bummer, gtg") // i dunno, lol
      .build();

    // When saved
    TwitchClientChatDataDto result = twitchClientChatDataService.save(request);

    // Then record count has increased
    assertThat(twitchClientChatDataRepository.count()).isEqualTo(2);

    // And it matches as expected
    TwitchClientChatDataDto retrieved = twitchClientChatDataService.findByOwner(request.getOwner()).get();
    assertThat(result).usingRecursiveComparison().isEqualTo(request);
    assertThat(result).usingRecursiveComparison().isEqualTo(retrieved);

  }

  @Test
  void nullOwner() {
    assertThatThrownBy(() -> {
      twitchClientChatDataService.save(TwitchClientChatDataDto.builder()
        .build());
    }).isInstanceOf(DataPersistenceException.class);
  }

  @Test
  void ownerOnlyAllowedOneSettingsSet() {

    // Given a setting is saved for an existing owner succeeds
    twitchClientChatDataService.save(TwitchClientChatDataDto.builder()
      .owner(existingTwitchClientData.getOwner())
      .build());

    // Then the count is the same as before
    assertThat(twitchClientChatDataRepository.count()).isEqualTo(1);

  }

  @Test
  void updateExistingSettings() {

    // Given settings exist
    assertThat(twitchClientChatDataRepository.count()).isEqualTo(1);

    // When updated
    TwitchClientChatDataDto request = TwitchClientChatDataDto.builder()
      .owner(existingTwitchClientData.getOwner())
      .botAccountUsername("johnny5")
      .broadcasterChannelUsername("80snerd")
      .connectionAttempts(78)
      .connectionTimeoutMs(1234)
      .trigger("|")
      .parseEntireMessage(false)
      .joinMessage("🤖 I'm alive! 🤖")
      .leaveMessage("Asta la vista! 👍")
      .build();
    TwitchClientChatDataDto result = twitchClientChatDataService.save(request);

    // Then the record count is the same
    assertThat(twitchClientChatDataRepository.count()).isEqualTo(1);

    // And the expected DTO is returned
    TwitchClientChatDataDto retrieved = twitchClientChatDataService.findByOwner(request.getOwner()).get();
    assertThat(result).usingRecursiveComparison().isEqualTo(request);
    assertThat(result).usingRecursiveComparison().isEqualTo(retrieved);

  }

}

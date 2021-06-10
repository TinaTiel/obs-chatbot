package com.tinatiel.obschatbot.core.client.twitch.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.data.client.twitch.auth.TwitchClientAuthDataConfig;
import com.tinatiel.obschatbot.data.client.twitch.auth.TwitchClientAuthDataService;
import com.tinatiel.obschatbot.data.client.twitch.auth.entity.TwitchClientAuthDataRepository;
import com.tinatiel.obschatbot.data.client.twitch.auth.model.TwitchClientAuthDataDto;
import com.tinatiel.obschatbot.security.owner.OwnerConfig;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@TestPropertySource(properties = {
  "com.tinatiel.twitch.auth.host=https://somehost.com",
  "com.tinatiel.twitch.auth.authorization-path=/authpath",
  "com.tinatiel.twitch.auth.token-path=/tokenpath",
  "com.tinatiel.twitch.auth.validation-path=/validationpath",
  "com.tinatiel.twitch.auth.scopes=bird:cockatiel,dog:doberman,cat:black",
  "com.tinatiel.twitch.auth.redirect-uri=http://localhost:8080/foo/bar"
})
@EnableConfigurationProperties
@ContextConfiguration(classes = {
  OwnerConfig.class,
  TwitchAuthConnectionSettingsFactory.class,
  TwitchClientAuthDataConfig.class
})
@SpringJUnitConfig
public class TwitchAuthConnectionSettingsFactoryTest {

  @MockBean
  TwitchClientAuthDataService twitchClientAuthDataService;

  @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
  OwnerService ownerService;

  @Autowired
  TwitchAuthConnectionSettingsFactory twitchAuthConnectionSettingsFactory;

  @MockBean
  TwitchClientAuthDataRepository twitchClientAuthDataRepository;

  @Test
  void settingsCombineWithDatabase() {

    // Given the database returns these settings
    TwitchClientAuthDataDto twitchClientAuthDataDto = TwitchClientAuthDataDto.builder()
      .clientId("correcthorse")
      .clientSecret("batterystaple")
      .build();
    when(twitchClientAuthDataService.findByOwner(any())).thenReturn(Optional.of(
      twitchClientAuthDataDto));

    // When called
    TwitchAuthConnectionSettings actual = twitchAuthConnectionSettingsFactory.getSettings();

    // Then the settings are a combo of the environment and db
    TwitchAuthConnectionSettings expected = TwitchAuthConnectionSettings.builder()
      .host("https://somehost.com")
      .authorizationPath("/authpath")
      .tokenPath("/tokenpath")
      .validationPath("/validationpath")
      .scopes(Arrays.asList("bird:cockatiel", "dog:doberman", "cat:black"))
      .redirectUri("http://localhost:8080/foo/bar")
      .clientId("correcthorse")
      .clientSecret("batterystaple")
      .build();
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

  }

  @Test
  void noSettingsFromDatabase() {

    // Given the database returns no settings
    when(twitchClientAuthDataService.findByOwner(any())).thenReturn(Optional.empty());

    // When called, then an exception is thrown
    assertThatThrownBy(() -> {
      twitchAuthConnectionSettingsFactory.getSettings();
    }).isInstanceOf(IllegalArgumentException.class);

  }
}

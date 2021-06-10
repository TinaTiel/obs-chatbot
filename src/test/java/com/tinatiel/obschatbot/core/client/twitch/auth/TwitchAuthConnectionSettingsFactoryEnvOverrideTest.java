package com.tinatiel.obschatbot.core.client.twitch.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
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
import java.util.UUID;
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
  "com.tinatiel.twitch.auth.redirect-uri=http://localhost:8080/foo/bar",
  "com.tinatiel.twitch.auth.client-id=myclient",
  "com.tinatiel.twitch.auth.client-secret=mysecret"
})
@EnableConfigurationProperties
@ContextConfiguration(classes = {
  OwnerConfig.class,
  TwitchAuthConnectionSettingsFactory.class,
  TwitchClientAuthDataConfig.class
})
@SpringJUnitConfig
public class TwitchAuthConnectionSettingsFactoryEnvOverrideTest {

  @MockBean
  TwitchClientAuthDataService twitchClientAuthDataService;

  @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
  OwnerService ownerService;

  @MockBean
  TwitchClientAuthDataRepository twitchClientAuthDataRepository;

  @Autowired
  TwitchAuthConnectionSettingsFactory twitchAuthConnectionSettingsFactory;

  @Test
  void environmentOverridesDatabase() {

    // Given the database returns these settings
    TwitchClientAuthDataDto twitchClientAuthDataDto = TwitchClientAuthDataDto.builder()
      .clientId("correcthorse")
      .clientSecret("batterystaple")
      .build();
    when(twitchClientAuthDataService.findByOwner(any())).thenReturn(Optional.of(
      twitchClientAuthDataDto));
    assertThat(twitchClientAuthDataService.findByOwner(UUID.randomUUID()).get())
      .usingRecursiveComparison().isEqualTo(twitchClientAuthDataDto);

    // When called
    TwitchAuthConnectionSettings actual = twitchAuthConnectionSettingsFactory.getSettings();

    // Then the settings ignore the db and use the environment
    TwitchAuthConnectionSettings expected = TwitchAuthConnectionSettings.builder()
      .host("https://somehost.com")
      .authorizationPath("/authpath")
      .tokenPath("/tokenpath")
      .validationPath("/validationpath")
      .scopes(Arrays.asList("bird:cockatiel", "dog:doberman", "cat:black"))
      .redirectUri("http://localhost:8080/foo/bar")
      .clientId("myclient")
      .clientSecret("mysecret")
      .build();
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

  }
}

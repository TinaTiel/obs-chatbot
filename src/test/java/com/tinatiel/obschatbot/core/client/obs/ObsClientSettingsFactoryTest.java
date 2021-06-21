package com.tinatiel.obschatbot.core.client.obs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.data.client.obs.ObsClientDataService;
import com.tinatiel.obschatbot.data.client.obs.model.ObsSettingsDto;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {ObsClientConfig.class})
@SpringJUnitConfig
public class ObsClientSettingsFactoryTest {

  @MockBean
  OwnerService ownerService;

  @MockBean
  ObsClientDataService dataService;

  @Autowired
  ObsClientSettingsFactory obsClientSettingsFactory;

  @BeforeEach
  void setUp() {
    when(ownerService.getOwner()).thenReturn(mock(OwnerDto.class));
  }

  @Test
  void getSettingsAsExpected() {

    // Given the data service returns data
    ObsSettingsDto data = ObsSettingsDto.builder()
      .host("localhost")
      .port(4567)
      .connectionTimeoutMs(1234)
      .password("supersecretpassword")
      .build();
    when(dataService.findByOwner(any())).thenReturn(Optional.of(data));

    // When called
    ObsClientSettings actual = obsClientSettingsFactory.getSettings();

    // Then the expected settings are returned
    ObsClientSettings expected = ObsClientSettings.builder()
      .host(data.getHost())
      .port(data.getPort())
      .connectionTimeoutMs(data.getConnectionTimeoutMs())
      .password(data.getPassword())
      .build();
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

  }

}

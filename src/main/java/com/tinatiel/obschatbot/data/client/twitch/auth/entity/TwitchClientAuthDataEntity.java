package com.tinatiel.obschatbot.data.client.twitch.auth.entity;

import com.tinatiel.obschatbot.data.common.OwnerEntity;
import javax.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
public class TwitchClientAuthDataEntity extends OwnerEntity {
  private String clientId;
  // https://twitch.uservoice.com/forums/310213-developers/suggestions/39785686-add-pkce-support-to-the-oauth2-0-authorization-cod
  // The alternative is a long-lived token, and in both cases file access would be required since
  // all is stored on the broadcaster's machine.
  // See https://discuss.dev.twitch.tv/t/long-lived-desktop-chat-application-oauth-token-secure-storage/32023
  private String clientSecret;
}

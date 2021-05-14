package com.tinatiel.obschatbot.core.client.twitch.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a successful response from Twitch at their user-data API. More data than below
 * is returned by Twitch, but we have no need for it so we don't include it.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsersDataResponse {

  String id;
  String login;
}

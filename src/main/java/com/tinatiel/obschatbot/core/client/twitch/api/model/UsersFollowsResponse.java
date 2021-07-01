package com.tinatiel.obschatbot.core.client.twitch.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the response from Twitch on a successful response getting the follows relationship
 * between two users. Since we only care about how many follows there are (1 or 0), we ignore the
 * entire data element that contains other user information.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsersFollowsResponse implements Serializable {

  private Integer total; // this is all we care about
}

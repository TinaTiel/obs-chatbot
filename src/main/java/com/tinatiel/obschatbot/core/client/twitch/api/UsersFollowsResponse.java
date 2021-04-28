package com.tinatiel.obschatbot.core.client.twitch.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class UsersFollowsResponse implements Serializable {
  private Integer total; // this is all we care about
}

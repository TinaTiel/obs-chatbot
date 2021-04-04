package com.tinatiel.obschatbot.core.user;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Period;

@Builder
@Getter
@ToString
public class UserDetails {
    private final boolean following;
    private final boolean patreon;
    @Builder.Default
    private final Period patreonDuration = Period.ZERO;
    private final boolean moderator;
}

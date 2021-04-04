package com.tinatiel.obschatbot.core.user;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Period;

@Builder
@Getter
@ToString
public class UserSecurityDetails {
    private final Boolean following;
    private final Boolean patron;
    private final Period patronPeriod;
    private final Boolean moderator;
    private final Boolean broadcaster;
}

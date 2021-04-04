/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.user;

import com.tinatiel.obschatbot.core.user.local.UserGroup;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@Getter
@Builder
public class User {

    private final Platform platform;

    private final String username;

    private final UserType userType;

    @Builder.Default
    private final Set<UserGroup> groups = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return platform == user.platform && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(platform, username);
    }

}

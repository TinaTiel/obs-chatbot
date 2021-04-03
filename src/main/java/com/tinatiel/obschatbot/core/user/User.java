/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.user;

import com.tinatiel.obschatbot.core.user.local.UserGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
public class User {

    private final Platform platform;
    private final String username;
    private final UserType userType;
    private final Set<UserGroup> groups = new HashSet<>();

    /**
     * Construct an user, defaulting to a GUEST type.
     */
    public User(Platform platform, String username) {
        this.platform = platform;
        this.username = username;
        this.userType = UserType.GUEST;
    }

    public User(Platform platform, String username, UserType userType, Set<UserGroup> groups) {
        this.platform = platform;
        this.username = username;
        this.userType = userType;
        this.groups.addAll(groups);
    }

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

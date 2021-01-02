/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.user;

import java.util.Objects;

public class User {

    private final Platform platform;
    private final String username;

    public User(Platform platform, String username) {
        this.platform = platform;
        this.username = username;
    }

    public Platform getPlatform() {
        return platform;
    }

    public String getUsername() {
        return username;
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

    @Override
    public String toString() {
        return "User{" +
                "platform=" + platform +
                ", username='" + username + '\'' +
                '}';
    }
}

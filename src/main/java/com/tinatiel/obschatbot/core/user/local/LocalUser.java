package com.tinatiel.obschatbot.core.user.local;

import com.tinatiel.obschatbot.core.user.Platform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores the most minimal information required to associate an
 * user with a locally-created group (for example, adding specific
 * subscribers to a 'special' group). Does not store other information
 * (follower status, subscribe $$$, etc.) both for GDPR reasons and
 * because this information is very transient and will change during a broadcast.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LocalUser {
    private Platform platform;
    private String username;
    private Set<UserGroup> groups;
}

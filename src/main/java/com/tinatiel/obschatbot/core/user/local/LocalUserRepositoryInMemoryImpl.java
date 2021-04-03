package com.tinatiel.obschatbot.core.user.local;

import com.tinatiel.obschatbot.core.user.Platform;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class LocalUserRepositoryInMemoryImpl implements LocalUserRepository {

    private Set<LocalUser> users = new HashSet<>();

    @Override
    public Optional<LocalUser> findByPlatformAndUsername(Platform platform, String username) {
        return users.stream()
                .filter(it -> it.getPlatform().equals(platform))
                .filter(it -> it.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public LocalUser save(LocalUser localUser) {
        users.add(localUser);
        return localUser;
    }
}

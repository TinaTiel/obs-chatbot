package com.tinatiel.obschatbot.core.user;

import com.tinatiel.obschatbot.core.client.twitch.api.TwitchApiClient;
import com.tinatiel.obschatbot.core.user.local.LocalUser;
import com.tinatiel.obschatbot.core.user.local.LocalUserRepository;
import com.tinatiel.obschatbot.core.user.local.UserGroup;

import java.util.HashSet;
import java.util.Set;

public class UserServiceImpl implements UserService {

    private final LocalUserRepository localUserRepository;
    private final TwitchApiClient twitchApiClient;

    public UserServiceImpl(LocalUserRepository localUserRepository, TwitchApiClient twitchApiClient) {
        this.localUserRepository = localUserRepository;
        this.twitchApiClient = twitchApiClient;
    }

    @Override
    public User findUserFromPartial(User partialUserInfo) {

        // Validate the minimum info is present
        if(partialUserInfo.getPlatform() == null || partialUserInfo.getUsername() == null) {
            throw new IllegalArgumentException("Username and Platform are required");
        }

        // Try to get local groups if they exist
        LocalUser localUser = localUserRepository.findByPlatformAndUsername(
                partialUserInfo.getPlatform(), partialUserInfo.getUsername())
                .orElse(new LocalUser());

        Set<UserGroup> groups = localUser.getGroups();
        groups.addAll(partialUserInfo.getGroups());

        // Determine the usertype, defaulting to what was specified in the partial userInfo
        UserType userType = partialUserInfo.getUserType();
        if(localUser.isBroadcaster()) {
            userType = UserType.BROADCASTER;
        } else {
            // To Do: Call the right service depending on the platform
            // todo
        }

        // Return the completed User
        return User.builder()
                .platform(partialUserInfo.getPlatform())
                .username(partialUserInfo.getUsername())
                .userType(userType)
                .groups(groups)
                .userDetails(partialUserInfo.getUserDetails())
                .build();

    }
}

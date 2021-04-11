package com.tinatiel.obschatbot.core.user.local;

/**
 * Handles all CRUD behaviors related to UserGroups.
 */
public interface UserGroupRepository {

  UserGroup save(UserGroup userGroup);
}

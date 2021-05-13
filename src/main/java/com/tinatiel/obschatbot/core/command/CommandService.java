/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.command;

import java.util.Optional;

/**
 * Responsible for CRUD operations of Commands.
 */
public interface CommandService {

  Optional<Command> findByName(String name);

}

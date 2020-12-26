package com.tinatiel.obschatbot.core.dispatch.chat;

import java.util.Optional;

public interface ChatMessageParser {
    Optional<ChatMessageParseResult> parse(String message);
}

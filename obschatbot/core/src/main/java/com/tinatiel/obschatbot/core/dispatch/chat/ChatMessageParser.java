package com.tinatiel.obschatbot.core.dispatch.chat;

public interface ChatMessageParser {
    ChatMessageParseResult parse(String message);
}

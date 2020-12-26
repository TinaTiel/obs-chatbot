package com.tinatiel.obschatbot.core.dispatch;

import java.util.concurrent.ExecutorService;

public interface CommandDispatcher {
    void submit(CommandRequest commandRequest);
}

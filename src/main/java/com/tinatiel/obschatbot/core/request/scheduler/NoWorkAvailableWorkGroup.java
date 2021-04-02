package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.CommandRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Special WorkGroup returned in the case a WorkGroupManager finds there is no work available.
 */
public class NoWorkAvailableWorkGroup implements WorkGroup {
    @Override
    public void add(CommandRequest commandRequest) {
        // do nothing
    }

    @Override
    public void free(ActionRequest actionRequest) {
        // do nothing
    }

    @Override
    public List<ActionRequest> getNextWorkBatch() {
        return new ArrayList<>();
    }

    @Override
    public int getNumberOfInflightRequests() {
        return 0;
    }

    @Override
    public int getNumberOfWorkableRequests() {
        return 0;
    }
}

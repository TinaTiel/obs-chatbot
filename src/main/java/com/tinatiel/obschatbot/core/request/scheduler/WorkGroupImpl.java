package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.CommandRequest;

import java.util.List;

public class WorkGroupImpl implements WorkGroup {


    @Override
    public void add(CommandRequest commandRequest) {

    }

    @Override
    public void free(ActionRequest actionRequest) {

    }

    @Override
    public List<ActionRequest> getNextWorkBatch() {
        return null;
    }

    @Override
    public int getNumberOfInflightRequests() {
        return 0;
    }
}

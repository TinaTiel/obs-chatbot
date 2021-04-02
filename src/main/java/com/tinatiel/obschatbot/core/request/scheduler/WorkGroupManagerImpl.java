package com.tinatiel.obschatbot.core.request.scheduler;

public class WorkGroupManagerImpl implements WorkGroupManager {

    private final WorkGroup noWork = new NoWorkAvailableWorkGroup();
    private final WorkGroupRouter router;

    public WorkGroupManagerImpl(WorkGroupRouter router) {
        this.router = router;
    }

    @Override
    public WorkGroup getNext() {

        return router.workGroupsByPriority().stream()
                .filter(it -> it.getNumberOfWorkableRequests() > 0)
                .findFirst()
                .orElse(noWork);

    }
}

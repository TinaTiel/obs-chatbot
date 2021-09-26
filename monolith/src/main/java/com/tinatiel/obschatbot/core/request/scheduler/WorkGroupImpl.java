package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.request.ActionCompleteEvent;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of WorkGroup that will generate batches of ActionRequests up to the maximum batch
 * size, iterating over the CommandRequests in the order received.
 */
@Slf4j
public class WorkGroupImpl implements WorkGroup {

  private final Timer timer = new Timer();
  private final ConcurrentLinkedQueue<CommandRequestWrapper> workableRequests =
      new ConcurrentLinkedQueue<>();
  private final ConcurrentHashMap<UUID, CommandRequestWrapper> blockedRequests =
      new ConcurrentHashMap<>();
  private final ReentrantLock lock = new ReentrantLock();
  private int maxBatchSize = 0;

  /**
   * Creates a workgroup with limitless concurrent commandRequest execution.
   */
  public WorkGroupImpl() {
  }

  /**
   * Creates a workgroup where batch size is limited to the specified number of
   * concurrentCommandRequests. Note this is only for one group! In a scheduler there may be
   * separate schedulers for broadcaster, moderator, and everyone else for example; the real total
   * number of concurrent executions is determined by this and should not be limited by the
   * scheduler itself.
   */
  public WorkGroupImpl(int maxBatchSize) {
    this.maxBatchSize = maxBatchSize;
  }

  @Override
  public void add(CommandRequest commandRequest) {
    workableRequests.add(new CommandRequestWrapper(commandRequest));
    log.trace("workable requests after adding commandRequest: " + workableRequests);
  }

  @Override
  public void free(UUID actionRequestId) {
    log.trace(this + " Freeing " + actionRequestId);
    lock.lock();
    try {
      log.trace(this + " workable requests before free: " + workableRequests);
      log.trace(this + " blocked requests before free: " + blockedRequests);
      // Get the request. If it doesn't exist, then exit
      CommandRequestWrapper blockedRequest = blockedRequests.get(actionRequestId);
      if (blockedRequest == null) {
        log.trace(this + " No request found, exiting");
        return;
      }

      // If the request has work to do, then add it back to workable requests
      if (!blockedRequest.getQueue().isEmpty()) {
        log.trace(this + " Found, adding back to workable requests");
        workableRequests.add(blockedRequest);
      }

      // Remove the request from the blocked requests
      log.trace(this + " Removing from blocked requests");
      blockedRequests.remove(actionRequestId);

      log.trace(this + " workable requests after free: " + workableRequests);
      log.trace(this + " blocked requests after free: " + blockedRequests);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public List<ActionRequest> getNextWorkBatch() {

    List<ActionRequest> batch = new ArrayList<>();
    List<CommandRequestWrapper> removeNext = new ArrayList<>();

    // Process the workable requests
    lock.lock();
    try {
      for (CommandRequestWrapper request : workableRequests) {

        // Get the next action
        ActionRequest nextAction = request.getQueue().poll();

        if (nextAction != null) {
          // If there is an action, then add it to the batch
          batch.add(nextAction);

          if (nextAction.requiresCompletion()) {
            // Actions requiring completion are added to the
            // block list and will be removed from workable requests
            blockedRequests.put(nextAction.getId(), request);
            removeNext.add(request);

            // We also must timeout the task if it takes too long so
            // it doesn't block forever
            log.trace(this + " Scheduling timeout for " + nextAction);
            long timeout = nextAction.getAction().getTimeout() < 0
                ? 1
                : nextAction.getAction().getTimeout();
            timer.schedule(new TimerTask() {
              @Override
              public void run() {
                log.trace(this + " Freeing from the timeout");
                free(nextAction.getId());
              }
            }, timeout);
          }
        }

        // If there is nothing left in the queue, then mark the request for removal
        if (request.getQueue().isEmpty()) {
          // If there is no action, then the request is empty
          removeNext.add(request);
        }

        //
        if (maxBatchSize > 0 && batch.size() >= maxBatchSize) {
          break;
        }

      }

      // Remove non-workable items from the workable requests
      removeNext.forEach(workableRequests::remove);
    } finally {
      lock.unlock();
    }

    return batch;
  }

  @Override
  public int getNumberOfInflightRequests() {
    lock.lock();
    int result;
    try {
      result = workableRequests.size() + blockedRequests.size();
    } finally {
      lock.unlock();
    }
    return result;
  }

  @Override
  public int getNumberOfWorkableRequests() {
    int result;
    lock.lock();
    try {
      result = workableRequests.size();
    } finally {
      lock.unlock();
    }
    return result;
  }

  @Override
  public void onEvent(ActionCompleteEvent event) {
    log.trace(this + " Freeing from event completion: " + event);
    free(event.getCompletedActionRequestId());
  }

  private static class CommandRequestWrapper {

    private final CommandRequest request;
    private final Queue<ActionRequest> actionRequestQueue = new LinkedList<>();

    public CommandRequestWrapper(CommandRequest request) {
      this.request = request;
      this.actionRequestQueue.addAll(request.getActionCommands());
      log.trace(this + " Created queue of actions: " + this.actionRequestQueue);
    }

    public Queue<ActionRequest> getQueue() {
      return actionRequestQueue;
    }

  }

}

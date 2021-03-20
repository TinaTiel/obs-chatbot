package com.tinatiel.obschatbot.core.infra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PausableQueueNotifierImpl {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock rLock = lock.readLock();
    private final Lock wLock = lock.writeLock();
    private final Condition runningCondition = wLock.newCondition();

    private final BlockingQueue queue;
    private final List<Listener> listeners = new ArrayList<>();

    private volatile boolean running = false;

    public PausableQueueNotifierImpl(BlockingQueue queue, Listener... listeners) {
        this.queue = queue;
        this.listeners.addAll(Arrays.asList(listeners));
        Executors.newSingleThreadExecutor().execute(() -> {
            while(true) {
                run();
            }
        });
    }

    public void pause() {
        running = false;
    }

    public void consume() {
        wLock.lock();
        try {
            running = true;
            runningCondition.signalAll(); // NOT notifyAll()! Use Condition signalAll() method
        } finally {
            wLock.unlock();
        }
    }

    private void run() {
        wLock.lock();
        try {
            try {
                // Sleep if not running.
                // Use a while loop instead of if/the to protect from spurious wakeups
                while(!running) {
                    runningCondition.await(); // NOT wait()! Use the Condition method await()
                }

                // Get next item out of the queue. Thread sleeps until an item is available, then wakes up with that item
                Object item = queue.take();
                if(running) {
                    notifyListeners(item); // If running, then notify listeners
                } else {
                    try {
                        queue.add(item); // If an item was taken but it isn't running, then put it back
                    } catch (IllegalStateException queueFullException) {
                        notifyListeners(item); // If the queue is full, we have no choice but to process it
                    }
                }
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        } finally {
            wLock.unlock();
        }

    }

    private void notifyListeners(Object item) {
        for(Listener listener:listeners) listener.onEvent(item);
    }

}

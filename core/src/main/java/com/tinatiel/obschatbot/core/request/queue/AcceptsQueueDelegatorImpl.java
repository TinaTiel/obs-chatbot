/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.queue;

import com.tinatiel.obschatbot.core.error.RequestNotAcceptableException;
import com.tinatiel.obschatbot.core.request.queue.type.ActionQueueType;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class AcceptsQueueDelegatorImpl implements AcceptsQueueDelegator {

    private final BlockingQueue<ActionCommand> delegate;
    private final ActionQueueType actionQueueType;

    public AcceptsQueueDelegatorImpl(BlockingQueue<ActionCommand> delegate, ActionQueueType actionQueueType) {
        this.delegate = delegate;
        this.actionQueueType = actionQueueType;
    }

    @Override
    public boolean add(ActionCommand actionCommand) {
        validateCanAccept(actionCommand);
        return delegate.add(actionCommand);
    }

    @Override
    public boolean offer(ActionCommand actionCommand) {
        validateCanAccept(actionCommand);
        return delegate.offer(actionCommand);
    }

    @Override
    public ActionCommand remove() {
        return delegate.remove();
    }

    @Override
    public ActionCommand poll() {
        return delegate.poll();
    }

    @Override
    public ActionCommand element() {
        return delegate.element();
    }

    @Override
    public ActionCommand peek() {
        return delegate.peek();
    }

    @Override
    public void put(ActionCommand actionCommand) throws InterruptedException {
        validateCanAccept(actionCommand);
        delegate.put(actionCommand);
    }

    @Override
    public boolean offer(ActionCommand actionCommand, long timeout, TimeUnit unit) throws InterruptedException {
        validateCanAccept(actionCommand);
        return delegate.offer(actionCommand, timeout, unit);
    }

    @Override
    public ActionCommand take() throws InterruptedException {
        return delegate.take();
    }

    @Override
    public ActionCommand poll(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.poll(timeout, unit);
    }

    @Override
    public int remainingCapacity() {
        return delegate.remainingCapacity();
    }

    @Override
    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends ActionCommand> c) {
        for(ActionCommand actionCommand:c) validateCanAccept(actionCommand);
        return delegate.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        for(Object obj:c) {
            if(obj instanceof ActionCommand) {
                validateCanAccept((ActionCommand) obj);
            } else {
                throw new RequestNotAcceptableException("Collection didn't contain ActionCommands: " + c, null);
            }
        }
        return delegate.retainAll(c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public Iterator<ActionCommand> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public int drainTo(Collection<? super ActionCommand> c) {
        return delegate.drainTo(c);
    }

    @Override
    public int drainTo(Collection<? super ActionCommand> c, int maxElements) {
        return delegate.drainTo(c, maxElements);
    }

    private void validateCanAccept(ActionCommand actionCommand) throws RequestNotAcceptableException {
        if(!actionQueueType.canAccept(actionCommand.getRecipient())) {
            throw new RequestNotAcceptableException(String.format(
            "Cannot accept actionCommand of type %s into queue %s", actionCommand.getRecipient(), this.getClass().getSimpleName())
            , null);
        }
    }
}

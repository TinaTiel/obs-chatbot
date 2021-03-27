/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.remove.queue;

import com.tinatiel.obschatbot.core.error.RequestNotAcceptableException;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.remove.queue.type.ActionQueueType;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AcceptsQueueDelegatorImpl implements AcceptsQueueDelegator {

    private final BlockingQueue<ActionRequest> delegate;
    private final ActionQueueType actionQueueType;

    public AcceptsQueueDelegatorImpl(BlockingQueue<ActionRequest> delegate, ActionQueueType actionQueueType) {
        this.delegate = delegate;
        this.actionQueueType = actionQueueType;
    }

    @Override
    public boolean add(ActionRequest actionRequest) {
        validateCanAccept(actionRequest);
        return delegate.add(actionRequest);
    }

    @Override
    public boolean offer(ActionRequest actionRequest) {
        validateCanAccept(actionRequest);
        return delegate.offer(actionRequest);
    }

    @Override
    public ActionRequest remove() {
        return delegate.remove();
    }

    @Override
    public ActionRequest poll() {
        return delegate.poll();
    }

    @Override
    public ActionRequest element() {
        return delegate.element();
    }

    @Override
    public ActionRequest peek() {
        return delegate.peek();
    }

    @Override
    public void put(ActionRequest actionRequest) throws InterruptedException {
        validateCanAccept(actionRequest);
        delegate.put(actionRequest);
    }

    @Override
    public boolean offer(ActionRequest actionRequest, long timeout, TimeUnit unit) throws InterruptedException {
        validateCanAccept(actionRequest);
        return delegate.offer(actionRequest, timeout, unit);
    }

    @Override
    public ActionRequest take() throws InterruptedException {
        return delegate.take();
    }

    @Override
    public ActionRequest poll(long timeout, TimeUnit unit) throws InterruptedException {
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
    public boolean addAll(Collection<? extends ActionRequest> c) {
        for(ActionRequest actionRequest :c) validateCanAccept(actionRequest);
        return delegate.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        for(Object obj:c) {
            if(obj instanceof ActionRequest) {
                validateCanAccept((ActionRequest) obj);
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
    public Iterator<ActionRequest> iterator() {
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
    public int drainTo(Collection<? super ActionRequest> c) {
        return delegate.drainTo(c);
    }

    @Override
    public int drainTo(Collection<? super ActionRequest> c, int maxElements) {
        return delegate.drainTo(c, maxElements);
    }

    private void validateCanAccept(ActionRequest actionRequest) throws RequestNotAcceptableException {
        if(!actionQueueType.canAccept(actionRequest.getRecipient())) {
            throw new RequestNotAcceptableException(String.format(
            "Cannot accept actionCommand of type %s into queue %s", actionRequest.getRecipient(), this.getClass().getSimpleName())
            , null);
        }
    }

    @Override
    public ActionQueueType getActionQueueType() {
        return actionQueueType;
    }

    @Override
    public boolean removeIf(Predicate<? super ActionRequest> filter) {
        return delegate.removeIf(filter);
    }

    @Override
    public Spliterator<ActionRequest> spliterator() {
        return delegate.spliterator();
    }

    @Override
    public Stream<ActionRequest> stream() {
        return delegate.stream();
    }

    @Override
    public Stream<ActionRequest> parallelStream() {
        return delegate.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super ActionRequest> action) {
        delegate.forEach(action);
    }
}

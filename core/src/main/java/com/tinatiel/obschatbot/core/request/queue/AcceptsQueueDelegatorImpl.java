/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.queue;

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
        return false;
    }

    @Override
    public boolean offer(ActionCommand actionCommand) {
        return false;
    }

    @Override
    public ActionCommand remove() {
        return null;
    }

    @Override
    public ActionCommand poll() {
        return null;
    }

    @Override
    public ActionCommand element() {
        return null;
    }

    @Override
    public ActionCommand peek() {
        return null;
    }

    @Override
    public void put(ActionCommand actionCommand) throws InterruptedException {

    }

    @Override
    public boolean offer(ActionCommand actionCommand, long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public ActionCommand take() throws InterruptedException {
        return null;
    }

    @Override
    public ActionCommand poll(long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }

    @Override
    public int remainingCapacity() {
        return 0;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends ActionCommand> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<ActionCommand> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public int drainTo(Collection<? super ActionCommand> c) {
        return 0;
    }

    @Override
    public int drainTo(Collection<? super ActionCommand> c, int maxElements) {
        return 0;
    }
}

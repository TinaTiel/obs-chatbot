package com.tinatiel.obschatbot.core.infra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Timeout(value = PausableQueueNotifierTest.TIMEOUT, unit = TimeUnit.MILLISECONDS)
public class PausableQueueNotifierTest {

    public final static long TIMEOUT = 500L;

    BlockingQueue<QueueItem> queue;
    QueueItem item1, item2, item3;
    Listener<QueueItem> listener1, listener2, listener3;

    @BeforeEach
    public void setUp() throws Exception {
        queue = new LinkedBlockingQueue<>();
        item1 = new QueueItem(1);
        item2 = new QueueItem(2);
        item3 = new QueueItem(3);
        listener1 = mock(Listener.class);
        listener2 = mock(Listener.class);
        listener3 = mock(Listener.class);
    }


    @Test
    public void pausedByDefault() {

        // Given the queue has items
        populateQueue();
        assertThat(queue).hasSize(3);

        // When a new consumer is created
        PausableQueueNotifierImpl consumer = new PausableQueueNotifierImpl(queue);
        consumer.addListener(listener1);
        consumer.addListener(listener2);
        consumer.addListener(listener3);

        // And we wait
        waitReasonably();

        // Then the queue still has items
        assertThat(queue).hasSize(3);
        assertListenerNotCalled(listener1);
        assertListenerNotCalled(listener2);
        assertListenerNotCalled(listener3);

    }

    @Test
    public void dontConsumeWhenPaused() {

        // Given a new consumer
        PausableQueueNotifierImpl consumer = new PausableQueueNotifierImpl(queue);
        consumer.addListener(listener1);
        consumer.addListener(listener2);
        consumer.addListener(listener3);

        // When we pause it
        consumer.pause();

        // And then add items to the queue
        populateQueue();
        assertThat(queue).hasSize(3);

        // And we wait
        waitReasonably();

        // Then the queue still has items
        assertThat(queue).hasSize(3);
        assertListenerNotCalled(listener1);
        assertListenerNotCalled(listener2);
        assertListenerNotCalled(listener3);

    }

    @Test
    public void consumeWhenResumed() {

        // Given a new consumer
        PausableQueueNotifierImpl consumer = new PausableQueueNotifierImpl(queue);
        consumer.addListener(listener1);
        consumer.addListener(listener2);
        consumer.addListener(listener3);

        // When we pause it
        consumer.pause();

        // And then add items to the queue
        populateQueue();
        assertThat(queue).hasSize(3);

        // And we wait
        waitReasonably();

        // And then we resume consumption
        consumer.consume();

        // And we wait
        waitReasonably();

        // Then the queue is empty
        assertThat(queue).isEmpty();
        assertListenerCalledWith(listener1, item1, item2, item3);
        assertListenerCalledWith(listener2, item1, item2, item3);
        assertListenerCalledWith(listener3, item1, item2, item3);

    }

    @Timeout(value = TIMEOUT*2, unit = TimeUnit.MILLISECONDS) // we wait longer since there are many pauses/resumes with waits between
    @Test
    public void pauseResumePauseResumeWorksAsExpected() {

        // Given a new consumer
        PausableQueueNotifierImpl consumer = new PausableQueueNotifierImpl(queue);
        consumer.addListener(listener1);
        consumer.addListener(listener2);
        consumer.addListener(listener3);

        // When we pause it
        consumer.pause();

        // And then add items to the queue
        queue.add(item1);

        // And we wait
        waitReasonably();
        assertThat(queue).hasSize(1);

        // And then we resume consumption
        consumer.consume();

        // And we wait for consumption to happen
        waitReasonably();

        // Then the queue is empty
        assertThat(queue).isEmpty();
        assertListenerCalledWith(listener1, item1);
        assertListenerCalledWith(listener2, item1);
        assertListenerCalledWith(listener3, item1);

        // And then when we pause
        consumer.pause();

        // And we wait for pause to happen, since this thread isn't synced to the consumer
        waitReasonably();

        // And then add items to the queue
        queue.add(item2);
        queue.add(item3);
        assertThat(queue).hasSize(2);

        // And we wait
        waitReasonably();
        assertThat(queue).hasSize(2); // item2 was taken due to take() resuming, but it should not have notified listeners
        assertListenerNotCalledWith(listener1, item2);
        assertListenerNotCalledWith(listener2, item2);
        assertListenerNotCalledWith(listener3, item2);

        // And then we resume consumption
        consumer.consume();

        // And we wait
        waitReasonably();

        // Then the queue is empty
        assertThat(queue).isEmpty();
        assertListenerCalledWith(listener1, item2, item3);
        assertListenerCalledWith(listener2, item2, item3);
        assertListenerCalledWith(listener3, item2, item3);

    }

    @Test
    public void regularQueueConsumerAlwaysConsumes() {

        // Given the queue is empty
        assertThat(queue).isEmpty();

        // When a new consumer is created
        QueueNotifierImpl consumer = new QueueNotifierImpl(queue);
        consumer.addListener(listener1);
        consumer.addListener(listener2);
        consumer.addListener(listener3);

        // And we wait
        waitReasonably();

        // And then we add items to the queue
        populateQueue();
        assertThat(queue).isNotEmpty(); // we cannot guarantee size here. It could be three, but should at least not be empty

        // And then wait
        waitReasonably();

        // Then the queue will have been consumed and listeners notified
        assertThat(queue).isEmpty();
        assertListenerCalledWith(listener1, item1, item2, item3);
        assertListenerCalledWith(listener2, item1, item2, item3);
        assertListenerCalledWith(listener3, item1, item2, item3);

    }

    void waitReasonably() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    void populateQueue() {
        queue.add(item1);
        queue.add(item2);
        queue.add(item3);
    }

    void assertListenerCalledWith(Listener listener, QueueItem... items) {
        for(QueueItem item:items) {
            verify(listener).onEvent(item);
        }
    }

    void assertListenerNotCalled(Listener listener) {
        verifyNoInteractions(listener);
    }

    void assertListenerNotCalledWith(Listener listener, QueueItem... items) {
        for(QueueItem item:items) {
            verify(listener, times(0)).onEvent(item);
        }
    }

    static class QueueItem {
        private final int num;

        public QueueItem(Integer num) {
            this.num = num;
        }

        @Override
        public String toString() {
            return "QueueItem " + num;
        }
    }

}

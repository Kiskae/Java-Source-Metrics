package nl.rug.jbi.jsm.util;

import com.google.common.collect.Maps;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Special log-appender that stores log entries internally so the program can consume them.
 *
 * @since 2014-05-30
 */
@Plugin(name = "Queue", category = "Core", elementType = "appender", printObject = true)
public class QueueLogAppender extends AbstractAppender {
    private final static int QUEUE_OVERFLOW_CAPACITY = 250;
    private final static Map<String, BlockingQueue<String>> QUEUES = Maps.newHashMap();
    private final static ReentrantReadWriteLock QUEUE_LOCK = new ReentrantReadWriteLock();
    private final BlockingQueue<String> queue;

    public QueueLogAppender(
            final String name, final Filter filter, final Layout<? extends Serializable> layout,
            final boolean ignoreExceptions, final BlockingQueue<String> queue
    ) {
        super(name, filter, layout, ignoreExceptions);
        this.queue = queue;
    }

    @PluginFactory
    public static QueueLogAppender createAppender(
            @PluginAttribute("name") final String name,
            @PluginAttribute("ignoreExceptions") final String ignore,
            @PluginElement("Layout") final Layout<? extends Serializable> layout,
            @PluginElement("Filters") final Filter filter
    ) {
        final boolean ignoreExceptions = Boolean.parseBoolean(ignore);
        if (name == null) {
            LOGGER.error("No name provided for QueueLogAppender");
            return null;
        }
        QUEUE_LOCK.writeLock().lock();
        final BlockingQueue<String> queue;
        try {
            if (QUEUES.containsKey(name)) {
                queue = QUEUES.get(name);
            } else {
                queue = new LinkedBlockingQueue<String>();
                QUEUES.put(name, queue);
            }
        } finally {
            QUEUE_LOCK.writeLock().unlock();
        }

        final Layout<? extends Serializable> finalLayout;
        if (layout != null) {
            finalLayout = layout;
        } else {
            finalLayout = PatternLayout.createLayout(null, null, null, null, null, null);
        }
        return new QueueLogAppender(name, filter, finalLayout, ignoreExceptions, queue);
    }

    public static String getNextLogEvent(final String queueName) {
        QUEUE_LOCK.readLock().lock();
        final BlockingQueue<String> queue;
        try {
            queue = QUEUES.get(queueName);
        } finally {
            QUEUE_LOCK.readLock().unlock();
        }

        if (queue != null) {
            try {
                return queue.take();
            } catch (InterruptedException ignored) {
            }
        }

        return null;
    }

    @Override
    public void append(final LogEvent logEvent) {
        if (this.queue.size() >= QUEUE_OVERFLOW_CAPACITY) {
            this.queue.clear(); //Prevent a buildup of messages due to lack of listener
        }
        this.queue.add(getLayout().toSerializable(logEvent).toString());
    }
}

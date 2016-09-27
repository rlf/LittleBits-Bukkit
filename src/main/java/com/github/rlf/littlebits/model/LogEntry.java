package com.github.rlf.littlebits.model;


import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

/**
 * A log entry for either device or account.
 */
public class LogEntry implements Comparable<LogEntry> {
    private final long timestamp;
    private final String message;

    public LogEntry(String message) {
        this(System.currentTimeMillis(), message);
    }

    public LogEntry(long timestamp, String message) {
        if (message == null) {
            throw new IllegalArgumentException("message cannot be null");
        }
        this.timestamp = timestamp;
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        long now = System.currentTimeMillis();
        return tr("\u00a77 - \u00a79{1} \u00a77({0})", dk.lockfuglsang.minecraft.util.TimeUtil.millisAsString(now-timestamp), tr(message));
    }

    /**
     * Natural ordering of LogEntries.
     */
    @Override
    public int compareTo(LogEntry o) {
        int cmp = (int)(o.timestamp - timestamp);
        if (cmp == 0) {
            cmp = message.compareTo(o.message);
        }
        return cmp;
    }
}

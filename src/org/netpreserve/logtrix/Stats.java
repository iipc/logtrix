package org.netpreserve.logtrix;

import java.time.Duration;
import java.time.Instant;

public class Stats {
    private long count;
    private long bytes;
    private long millis;
    private Instant lastSeen;

    Stats() {}

    void add(CrawlDataItem item) {
        count++;
        bytes += item.getSize();
        Duration captureDuration = item.getCaptureDuration();
        if (captureDuration != null) {
            millis += captureDuration.toMillis();
        }
        lastSeen = item.getCaptureBegan();
    }

    @Override
    public String toString() {
        return "count=" + count +
                " bytes=" + bytes +
                " millis=" + millis +
                " lastSeen=" + lastSeen;
    }

    public long getCount() {
        return count;
    }

    public long getBytes() {
        return bytes;
    }

    public long getMillis() {
        return millis;
    }

    public Instant getLastSeen() {
        return lastSeen;
    }
}
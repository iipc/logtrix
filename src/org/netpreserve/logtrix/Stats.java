package org.netpreserve.logtrix;

import java.time.Duration;
import java.time.Instant;

public class Stats {
    long count;
    long bytes;
    long millis;
    Instant lastSeen;

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
}
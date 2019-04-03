package org.netpreserve.logtrix;

import java.time.Duration;
import java.time.Instant;

public class Stats {
    private long count;
    private long bytes;
    private long millis;
    private Instant firstTime;
    private Instant lastTime;

    Stats() {}

    void add(CrawlDataItem item) {
        count++;
        bytes += item.getSize();
        Duration captureDuration = item.getCaptureDuration();
        if (captureDuration != null) {
            millis += captureDuration.toMillis();
        }
        Instant time = item.getCaptureBegan();
        if (time != null) {
            if (firstTime == null || time.isBefore(firstTime)) {
                firstTime = time;
            }
            if (lastTime == null || time.isAfter(lastTime)) {
                lastTime = time;
            }
        }
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

    public Instant getLastTime() {
        return lastTime;
    }

    public Instant getFirstTime() {
        return firstTime;
    }
}
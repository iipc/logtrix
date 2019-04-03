package org.netpreserve.logtrix;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class CrawlSummary {

    private final Map<Integer, Stats> statusCodes;
    private final Map<String, Stats> mimeTypes;

    public CrawlSummary(Map<Integer, Stats> statusCodes, Map<String, Stats> mimeTypes) {
        this.statusCodes = statusCodes;
        this.mimeTypes = mimeTypes;
    }

    public static CrawlSummary build(CrawlLogIterator log) {
        Map<Integer, Stats> statusCodes = new HashMap<>();
        Map<String, Stats> mimeTypes = new HashMap<>();

        for (CrawlDataItem item : log) {
            mimeTypes.computeIfAbsent(item.getMimeType(), m -> new Stats()).add(item);
            statusCodes.computeIfAbsent(item.getStatusCode(), s -> new Stats()).add(item);
        }

        return new CrawlSummary(statusCodes, mimeTypes);
    }

    public void print(PrintStream out) {
        for (Map.Entry<String, Stats> entry: getMimeTypes().entrySet()) {
            out.println(entry.getKey() + " " + entry.getValue());
        }
    }

    public Map<Integer, Stats> getStatusCodes() {
        return statusCodes;
    }

    public Map<String, Stats> getMimeTypes() {
        return mimeTypes;
    }
}

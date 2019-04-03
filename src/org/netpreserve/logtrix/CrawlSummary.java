package org.netpreserve.logtrix;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.netpreserve.logtrix.CrawlLogUtils.canonicalizeMimeType;

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
            String mimeType = canonicalizeMimeType(item.getMimeType());
            mimeTypes.computeIfAbsent(mimeType, m -> new Stats()).add(item);
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

    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try (CrawlLogIterator log = new CrawlLogIterator(Paths.get(args[0]))) {
            CrawlSummary summary = CrawlSummary.build(log);
            objectMapper.writeValue(System.out, summary);
        }
    }
}

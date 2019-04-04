package org.netpreserve.logtrix;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Utility methods for working with HTTP and Heritrix status codes.
 */
public class StatusCodes {
    private static Map<Integer,String> descriptions = loadStatusCodes();

    private static Map<Integer, String> loadStatusCodes() {
        Map<Integer,String> map = new HashMap<>();
        try {
            InputStream stream = StatusCodes.class.getResourceAsStream("status-codes.txt");
            if (stream == null) {
                throw new RuntimeException("org/netpreserve/logtrix/status-codes.txt not foung on classpath");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, UTF_8))) {
                while (true) {
                    String line  = reader.readLine();
                    if (line == null) return map;
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] fields = line.split(" ", 2);
                    map.put(Integer.valueOf(fields[0]), fields[1]);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to initailize status codes list", e);
        }
    }

    /**
     * Given a status code from a crawl log returns human readable description. Returns null if the status code
     * is unknown.
     */
    public static String describe(int statusCode) {
        return descriptions.get(statusCode);
    }

    public static boolean isInformational(int statusCode) {
        return statusCode >= 100 && statusCode <= 199;
    }

    public static boolean isSuccessful(int statusCode) {
        return statusCode >= 200 && statusCode <= 299;
    }

    public static boolean isRedirection(int statusCode) {
        return statusCode >= 300 && statusCode <= 399;
    }

    public static boolean isClientError(int statusCode) {
        return statusCode >= 400 && statusCode <= 499;
    }

    public static boolean isServerError(int statusCode) {
        return statusCode >= 500 && statusCode <= 599;
    }

    public static boolean isCrawlerError(int statusCode) {
        return statusCode < 0;
    }

    public static boolean isError(int statusCode) {
        return isClientError(statusCode) || isServerError(statusCode) || isCrawlerError(statusCode);
    }
}

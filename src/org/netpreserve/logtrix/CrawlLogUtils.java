package org.netpreserve.logtrix;

import java.util.Locale;

public class CrawlLogUtils {

    public static String canonicalizeMimeType(String mimeType) {
        int i = mimeType.indexOf(';');
        if (i >= 0) {
            mimeType = mimeType.substring(0, i);
        }
        return mimeType.trim().toLowerCase(Locale.US);
    }

}

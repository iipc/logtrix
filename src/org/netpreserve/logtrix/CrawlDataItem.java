/* CrawlDataItem
 * 
 * Created on 10.04.2006
 *
 * Copyright (C) 2006 National and University Library of Iceland
 * 
 * This file is part of the DeDuplicator (Heritrix add-on module).
 * 
 * DeDuplicator is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * any later version.
 * 
 * DeDuplicator is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser Public License
 * along with DeDuplicator; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.netpreserve.logtrix;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Duration;
import java.time.Instant;

/**
 * A base class for individual items of crawl data that should be added to the
 * index.
 * 
 * @author Kristinn Sigur&eth;sson
 */
public final class CrawlDataItem {
    private String URL;
    private String parentURL;
    private String statusCode;
    private String contentDigest;
    private String timestamp;
    private String hoppath;
    private String etag;
    private String mimeType;
    private boolean duplicate;
    private long size;
    private String originalCrawlLogLine;
    private Instant captureBegan;
    private Duration duration;
    private JsonNode extraInfo;

    /**
     * Constructor. Creates a new CrawlDataItem with all its data initialized
     * to null.
     */
    CrawlDataItem() {
        URL = null;
        contentDigest = null;
        timestamp = null;
        etag = null;
        mimeType = null;
        duplicate = false;
        size = -1;
    }

    public String getURL() {
        return URL;
    }

    void setURL(String URL) {
        this.URL = URL;
    }

    public String getParentURL() {
        return parentURL;
    }

    void setParentURL(String parentURL) {
        this.parentURL = parentURL;
    }

    public String getStatusCode() {
        return statusCode;
    }

    void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getContentDigest() {
        return contentDigest;
    }

    void setContentDigest(String contentDigest) {
        this.contentDigest = contentDigest;
    }

    public String getTimestamp() {
        return timestamp;
    }

    void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getHoppath() {
        return hoppath;
    }

    void setHoppath(String hoppath) {
        this.hoppath = hoppath;
    }

    public String getEtag() {
        return etag;
    }

    void setEtag(String etag) {
        this.etag = etag;
    }

    public String getMimeType() {
        return mimeType;
    }

    void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public long getSize() {
        return size;
    }

    void setSize(long size) {
        this.size = size;
    }

    public String getOriginalCrawlLogLine() {
        return originalCrawlLogLine;
    }

    void setOriginalCrawlLogLine(String originalCrawlLogLine) {
        this.originalCrawlLogLine = originalCrawlLogLine;
    }

    void setExtraInfo(JsonNode extraInfo) {
        this.extraInfo = extraInfo;
    }

    public JsonNode getExtraInfo() {
        return extraInfo;
    }

    public Instant getCaptureBegan() {
        return captureBegan;
    }

    void setCaptureBegan(Instant captureBegan) {
        this.captureBegan = captureBegan;
    }

    public Duration getCaptureDuration() {
        return duration;
    }

    void setDuration(Duration duration) {
        this.duration = duration;
    }
}

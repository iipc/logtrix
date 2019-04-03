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

/**
 * A base class for individual items of crawl data that should be added to the
 * index.
 * 
 * @author Kristinn Sigur&eth;sson
 */
public class CrawlDataItem {
    
    /**
     * The proper formating of {@link #setURL(String)} and {@link #getURL()}
     */
	public static final String dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";
    
    protected String URL;
    protected String parentURL;
    protected String statusCode;
    protected String contentDigest;
    protected String timestamp;
    protected String hoppath;
    protected String etag;
    protected String mimeType;
    protected boolean duplicate;
    protected long size;
    protected String originalCrawlLogLine;
    protected Boolean isProbablyNonDynamic;
    private JsonNode extraInfo;

    /**
     * Constructor. Creates a new CrawlDataItem with all its data initialized
     * to null.
     */
    public CrawlDataItem(){
        URL = null;
        contentDigest = null;
        timestamp = null;
        etag = null;
        mimeType = null;
        duplicate = false;
        size = -1;
    }
	
	public boolean isProbablyNonDynamic() {
		if (isProbablyNonDynamic!=null) {
			return isProbablyNonDynamic;
		}
		
		if (getSize()==0
				|| getMimeType().equals("text/html")
				|| getMimeType().equals("unknown")
				|| getMimeType().equals("text/calendar")
				|| getMimeType().equals("text/xml")
				|| getMimeType().equals("application/rss+xml")
				|| getMimeType().equals("application/atom+xml")
				|| getMimeType().equals("application/json")
				|| getStatusCode().equals("-5000")
				|| getURL().matches("^.*captcha.*$")
				|| getURL().matches("^.*lightbox.js.*$")
				|| getURL().matches("^.*image.php?target=image_verification.*$")
				) {
			isProbablyNonDynamic = false;
			return false;
		}
		
		isProbablyNonDynamic = true;
		return true;
	}


    public static String getDateFormat() {
        return dateFormat;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getParentURL() {
        return parentURL;
    }

    public void setParentURL(String parentURL) {
        this.parentURL = parentURL;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getContentDigest() {
        return contentDigest;
    }

    public void setContentDigest(String contentDigest) {
        this.contentDigest = contentDigest;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getHoppath() {
        return hoppath;
    }

    public void setHoppath(String hoppath) {
        this.hoppath = hoppath;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getOriginalCrawlLogLine() {
        return originalCrawlLogLine;
    }

    public void setOriginalCrawlLogLine(String originalCrawlLogLine) {
        this.originalCrawlLogLine = originalCrawlLogLine;
    }

    public void setExtraInfo(JsonNode extraInfo) {
        this.extraInfo = extraInfo;
    }

    public JsonNode getExtraInfo() {
        return extraInfo;
    }
}

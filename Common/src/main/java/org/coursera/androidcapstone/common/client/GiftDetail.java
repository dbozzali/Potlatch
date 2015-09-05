package org.coursera.androidcapstone.common.client;

import com.google.common.base.Objects;
import com.google.common.io.BaseEncoding;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.Lob;

public class GiftDetail implements Serializable {
	private static final long serialVersionUID = -1103085105943052273L;

	public long id;
	public String title;
	public String text;
	@Lob @Basic(fetch=FetchType.LAZY)
	public String content;
	public String createdByUsername;
	public long creationTimestamp;
	public int touches;
	public int flags;
	public boolean userTouchedBy;
	public boolean userFlagged;

	public GiftDetail() {
		this(0L, "", "", "", "", 0L, 0, 0, false, false);
	}

	public GiftDetail(long id, String title, String text, String content, String createdByUsername, long creationTimestamp, int touches, int flags, boolean userTouchedBy, boolean userFlagged) {
		super();
		this.id = id;
		this.title = title;
		this.text = text;
		this.content = content;
		this.createdByUsername = createdByUsername;
		this.creationTimestamp = creationTimestamp;
		this.touches = touches;
		this.flags = flags;
		this.userTouchedBy = userTouchedBy;
		this.userFlagged = userFlagged;
	}

	@Lob @Basic(fetch=FetchType.LAZY)
	public byte[] contentAsByteArray() {
		if (this.content != null) {
            return BaseEncoding.base64().decode(this.content);
		}
		return null;
	}

	public String creationTimestampAsDateString() {
        // Create a DateFormatter object for displaying date in specified format
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        // Create a calendar object that will convert the date and time value in milliseconds to date
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.creationTimestamp);
        return formatter.format(calendar.getTime());
	}

	/**
	 * Two GiftDetail objets will generate the same hashcode if they have exactly
     * the same values for their attributes.
	 */
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(this.id, this.title, this.text, this.content, this.createdByUsername, this.creationTimestamp, this.touches, this.flags, this.userTouchedBy, this.userFlagged);
	}

	/**
	 * Two GiftDetail objects are considered equal if they have exactly the same
     * values for their attributes.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GiftDetail) {
            GiftDetail other = (GiftDetail) obj;
			// Google Guava provides great utilities for equals too!
			return (this.id == other.id)
					&& Objects.equal(this.title, other.title)
					&& Objects.equal(this.text, other.text)
					&& Objects.equal(this.content, other.content)
					&& Objects.equal(this.createdByUsername, other.createdByUsername)
					&& (this.creationTimestamp == other.creationTimestamp)
					&& (this.touches == other.touches)
					&& (this.flags == other.flags)
					&& (this.userTouchedBy == other.userTouchedBy)
					&& (this.userFlagged == other.userFlagged);
		}
		else {
			return false;
		}
	}
}

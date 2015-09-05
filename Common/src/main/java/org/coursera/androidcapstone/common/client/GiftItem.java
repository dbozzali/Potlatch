package org.coursera.androidcapstone.common.client;

import com.google.common.base.Objects;
import com.google.common.io.BaseEncoding;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.Lob;

public class GiftItem implements Serializable {
	private static final long serialVersionUID = -2529092817715976521L;

 	public long id;
	public String title;
	@Lob @Basic(fetch=FetchType.LAZY)
    public String thumbnail;

	public GiftItem() {
		this(0L, "", "");
	}

	public GiftItem(long id, String title, String thumbnail) {
		super();
		this.id = id;
		this.title = title;
        this.thumbnail = thumbnail;
	}

	@Lob @Basic(fetch=FetchType.LAZY)
	public byte[] thumbnailAsByteArray() {
		if (this.thumbnail != null) {
            return BaseEncoding.base64().decode(this.thumbnail);
		}
		return null;
	}

	/**
	 * Two GiftItem objets will generate the same hashcode if they have exactly
     * the same values for their attributes.
	 */
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(this.id, this.title);
	}

	/**
	 * Two GiftItem objects are considered equal if they have exactly the same
     * values for their attributes.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GiftItem) {
            GiftItem other = (GiftItem) obj;
			// Google Guava provides great utilities for equals too!
			return (this.id == other.id)
					&& Objects.equal(this.title, other.title)
					&& Objects.equal(this.thumbnail, other.thumbnail);
		}
		else {
			return false;
		}
	}
}

package org.coursera.androidcapstone.common.client;

import com.google.common.base.Objects;

import java.io.Serializable;

public class GiftCounters implements Serializable {
	private static final long serialVersionUID = -1893643656199616463L;

	public long id;
	public int touches;
    public int flags;

	public GiftCounters() {
		this(0L, 0, 0);
	}

	public GiftCounters(long id, int touches, int flags) {
		super();
		this.id = id;
		this.touches = touches;
        this.flags = flags;
	}
	
	/**
	 * Two GiftCounters objets will generate the same hashcode if they have exactly
     * the same values for their attributes.
	 */
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(this.id, this.touches, this.flags);
	}

	/**
	 * Two GiftCounters objects are considered equal if they have exactly the same
     * values for their attributes.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GiftCounters) {
            GiftCounters other = (GiftCounters) obj;
			// Google Guava provides great utilities for equals too!
			return (this.id == other.id)
					&& (this.touches == other.touches)
                    && (this.flags == other.flags);
		}
		else {
			return false;
		}
	}
}

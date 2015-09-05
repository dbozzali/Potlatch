package org.coursera.androidcapstone.common.client;

import com.google.common.base.Objects;
import com.google.common.io.BaseEncoding;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.Lob;

public class TopGiftGiver implements Serializable {
	private static final long serialVersionUID = 8392899839717539879L;

	public String username;
    @Lob @Basic(fetch=FetchType.LAZY)
    public String avatar;
	public long touches;

	public TopGiftGiver() {
		this("", "", 0);
	}

	public TopGiftGiver(String username, String avatar, long touches) {
		super();
		this.username = username;
        this.avatar = avatar;
		this.touches = touches;
	}

	@Lob @Basic(fetch=FetchType.LAZY)
	public byte[] avatarAsByteArray() {
		if (this.avatar != null) {
            return BaseEncoding.base64().decode(this.avatar);
		}
		return null;
	}

	/**
	 * Two TopGiftGiver objects will generate the same hashcode if they have exactly
     * the same values for their attributes.
	 */
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(this.username, this.avatar, this.touches);
	}

	/**
	 * Two TopGiftGiver objects are considered equal if they have exactly the same
     * values for their attributes.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TopGiftGiver) {
            TopGiftGiver other = (TopGiftGiver) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(this.username, other.username)
                    && Objects.equal(this.avatar, other.avatar)
					&& (this.touches == other.touches);
		}
		else {
			return false;
		}
	}
}

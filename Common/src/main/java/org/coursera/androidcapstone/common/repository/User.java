package org.coursera.androidcapstone.common.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * A simple object to represent a User and its attributes
 */

@Entity
@Table(name="Users")
public class User implements Serializable {
	private static final long serialVersionUID = 4495287330442263027L;

	@Id
	@Basic
	@Column(name="Username",updatable=false,nullable=false,length=32)
	private String username;

    @Column(name="Avatar")
    @Lob @Basic(fetch=FetchType.LAZY)
    private String avatar;

	// We ask Jackson to ignore this property when serializing
	// a User to avoid the problem of circular references
	// in our JSON. Since this is a bi-directional relationship, 
	// if we didn't ignore this property, Jackson would generate
	// a stack overflow by trying to serialize the Gifts of the
	// User, which in turn refer back to the User, which
	// would refer back to the Gifts, etc. creating an infinite
	// loop. By ignoring this property, we break the chain.
	//
	// If you use Spring Data REST, it handles this for you 
	// automatically and inserts links into the generated JSON
	// to reference the associated objects.
	@JsonIgnore
	@OneToMany(mappedBy="createdBy",fetch=FetchType.LAZY)
	private Set<Gift> gifts;

	@Basic
	@Column(name="TotalTouchesCount",nullable=false)
	private long totalTouchesCount;

	public User() {
		this("", "");
	}

	public User(String username, String avatar) {
		super();
		this.username = username;
        this.avatar = avatar;
		this.gifts = new HashSet<Gift>();
		this.totalTouchesCount = 0L;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

    @Lob @Basic(fetch=FetchType.LAZY)
    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Lob @Basic(fetch=FetchType.LAZY)
    public byte[] avatarAsByteArray() {
        if (this.avatar != null) {
            return BaseEncoding.base64().decode(this.avatar);
        }
        return null;
    }

    public void avatarFromByteArray(byte[] avatar) {
        this.avatar = "";
        if (avatar != null) {
            this.avatar = BaseEncoding.base64().encode(avatar);
        }
    }

	public Collection<Gift> giftsAsList() {
		return Lists.newArrayList(this.gifts.iterator());
	}

	public Set<Gift> getGifts() {
		return this.gifts;
	}

	public void setGifts(Set<Gift> gits) {
		this.gifts = new HashSet<Gift>(gifts);
	}

	public long getGiftsCount() {
		return this.gifts.size();
	}

	public long getTotalTouchesCount() {
		return this.totalTouchesCount;
	}
	
	public void setTotalTouchesCount(long count) {
		this.totalTouchesCount = count;
	}
	
	public void addTouch() {
		this.totalTouchesCount++;
	}
	
	public void removeTocuh() {
		if (this.totalTouchesCount > 0) {
			this.totalTouchesCount--;
		}
	}
	
	public void updateTotalTouches() {
		long totalTouches = 0;
		for (Gift g : this.gifts) {
			totalTouches += g.getTouchesCount();
		}
		this.totalTouchesCount = totalTouches;
	}
	
	/**
	 * Two User will generate the same hashcode if they have exactly the same
	 * values for their attributes
	 * 
	 */
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(this.username, this.avatar);
	}

	/**
	 * Two User are considered equal if they have exactly the same values for
	 * their attributes.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User) {
            User other = (User) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(this.username, other.username)
                    && Objects.equal(this.avatar, other.avatar);
		}
		else {
			return false;
		}
	}
}

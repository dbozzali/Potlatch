package org.coursera.androidcapstone.common.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;

import org.coursera.androidcapstone.common.client.UserTouchFlagStatus;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * A simple object to represent a gift and its attributes
 */

@Entity
@Table(name="Gifts")
//Define a sequence - might also be in another class
@SequenceGenerator(name="GiftSequenceGenerator",initialValue=1,allocationSize=100)
public class Gift implements Serializable {
	private static final long serialVersionUID = -6739775147051940663L;

	@Id
	@Column(name="Id",updatable=false,nullable=false)
	// Use the sequence that is defined above
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="GiftSequenceGenerator")
	private long id;

	@Basic
	@Column(name="Title",updatable=false,nullable=false)
	private String title;

	@Basic
	@Column(name="Text",updatable=false,nullable=false,length=1024)
	private String text;

	@Column(name="Content",updatable=false,nullable=false)
	@Lob @Basic(fetch=FetchType.LAZY)
	private String content;

	@Column(name="Thumbnail",updatable=false,nullable=false)
	@Lob @Basic(fetch=FetchType.LAZY)
	private String thumbnail;

	// We ask Jackson to ignore this property when serializing
	// a Gift to avoid the problem of circular references
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
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CreatedBy",updatable=false,nullable=false)
	private User createdBy;

	@Basic
	@Column(name="CreationTimestamp",updatable=false,nullable=false)
	private long creationTimestamp;

	// We ask Jackson to ignore this property when serializing
	// a Gift to avoid the problem of circular references
	// in our JSON. Since this is a bi-directional relationship, 
	// if we didn't ignore this property, Jackson would generate
	// a stack overflow by trying to serialize the GiftChain of the
	// Gift, which in turn refer back to the Gift, which
	// would refer back to the Gifts, etc. creating an infinite
	// loop. By ignoring this property, we break the chain.
	//
	// If you use Spring Data REST, it handles this for you 
	// automatically and inserts links into the generated JSON
	// to reference the associated objects.
	@JsonIgnore
	@ManyToOne(fetch=FetchType.EAGER,cascade=CascadeType.PERSIST)
	@JoinColumn(name="GiftChain",updatable=false,nullable=false)
	private Gift giftChain;

	// We ask Jackson to ignore this property when serializing
	// a Gift to avoid the problem of circular references
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
	@ManyToMany
	@JoinTable(
		name="TouchedUsers",
		inverseJoinColumns=@JoinColumn(name="Username", referencedColumnName="Username"),
		joinColumns=@JoinColumn(name="GiftId", referencedColumnName="Id")
	)
	private Set<User> touchedUsers;

	@Basic
	@Column(name="TouchesCount",nullable=false)
	private int touchesCount;

	// We ask Jackson to ignore this property when serializing
	// a Gift to avoid the problem of circular references
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
	@ManyToMany
	@JoinTable(
		name="FlaggedByUsers",
		inverseJoinColumns=@JoinColumn(name="Username", referencedColumnName="Username"),
		joinColumns=@JoinColumn(name="GiftId", referencedColumnName="Id")
	)
	private Set<User> flaggedByUsers;

	@Basic
	@Column(name="FlagsCount",nullable=false)
	private int flagsCount;

	public Gift() {
		this("", "", "", "", null, 0L, null);
	}

	public Gift(String title, String text, String content, String thumbnail, User createdBy, long creationTimestamp, Gift giftChain) {
		super();
		this.title = title;
		this.text = text;
		this.content = content;
		this.thumbnail = thumbnail;
		this.createdBy = createdBy;
		this.creationTimestamp = creationTimestamp;
		this.giftChain = giftChain;
		this.touchedUsers = new HashSet<User>();
		this.touchesCount = 0;
		this.flaggedByUsers = new HashSet<User>();
		this.flagsCount = 0;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Lob @Basic(fetch=FetchType.LAZY)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Lob @Basic(fetch=FetchType.LAZY)
	public byte[] contentAsByteArray() {
		if (this.content != null) {
            return BaseEncoding.base64().decode(this.content);
		}
		return null;
	}

	public void contentFromByteArray(byte[] content) {
        this.content = "";
		if (content != null) {
            this.content = BaseEncoding.base64().encode(content);
		}
	}
	
	@Lob @Basic(fetch=FetchType.LAZY)
	public String getThumbnail() {
		return this.thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	@Lob @Basic(fetch=FetchType.LAZY)
	public byte[] thumbnailAsByteArray() {
		if (this.thumbnail != null) {
            return BaseEncoding.base64().decode(this.thumbnail);
		}
		return null;
	}

    public void thumbnailFromByteArray(byte[] thumbnail) {
        this.thumbnail = "";
        if (thumbnail != null) {
            this.thumbnail = BaseEncoding.base64().encode(thumbnail);
        }
    }

	public User getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
	
	public long getCreationTimestamp() {
		return this.creationTimestamp;
	}

	public void setCreationTimestamp(long timestamp) {
		this.creationTimestamp = timestamp;
	}

	public String creationTimestampAsDateString() {
        // Create a DateFormatter object for displaying date in specified format
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        // Create a calendar object that will convert the date and time value in milliseconds to date
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.creationTimestamp);
        return formatter.format(calendar.getTime());
	}
	
	public Gift getGiftChain() {
		return this.giftChain;
	}

	public void setGiftChain(Gift chain) {
		this.giftChain = chain;
	}
	
	public boolean addTouchedUser(User user) {
		boolean retVal = this.touchedUsers.add(user);
		this.setTouchesCount(this.touchedUsers.size());
		this.flaggedByUsers.remove(user);
		this.setFlagsCount(this.flaggedByUsers.size());
		if (retVal) {
			this.createdBy.addTouch();
		}
		return retVal;
	}

	public boolean removeTouchedUser(User user) {
		boolean retVal = this.touchedUsers.remove(user);
		this.setTouchesCount(this.touchedUsers.size());
		if (retVal) {
			this.createdBy.removeTocuh();
		}
		return retVal;
	}
	
	public boolean touchedUser(User user) {
		return this.touchedUsers.contains(user);
	}

	public Collection<String> touchedUsersAsList() {
        List<String> list = Lists.newArrayList();
        for (User u : this.touchedUsers) {
            list.add(u.getUsername());
        }
        return list;
    }

	public Set<User> getTouchedUsers() {
		return this.touchedUsers;
	}

	public void setTouchedUsers(Set<User> users) {
		this.touchedUsers = new HashSet<User>(users);
	}

	public int getTouchesCount() {
		return this.touchesCount;
	}
	
	protected void setTouchesCount(int count) {
		this.touchesCount = count;
	}

	public boolean addUserFlag(User user) {
		boolean retVal = this.flaggedByUsers.add(user);
		this.setFlagsCount(this.flaggedByUsers.size());
		boolean checkTouch = this.touchedUsers.remove(user);
		this.setTouchesCount(this.touchedUsers.size());
		if (checkTouch) {
			this.createdBy.removeTocuh();
		}
		return retVal;
	}

	public boolean removeUserFlag(User user) {
		boolean retVal = this.flaggedByUsers.remove(user);
		this.setFlagsCount(this.flaggedByUsers.size());
		return retVal;
	}

	public boolean flaggedByUser(User user) {
		return this.flaggedByUsers.contains(user);
	}

	public Collection<String> flaggedByUsersAsList() {
        List<String> list = Lists.newArrayList();
        for (User u : this.flaggedByUsers) {
            list.add(u.getUsername());
        }
		return list;
	}

	public Set<User> getFlaggedByUsers() {
		return this.flaggedByUsers;
	}

	public void setFlaggedByUsers(Set<User> users) {
		this.flaggedByUsers = new HashSet<User>(users);
		this.setFlagsCount(this.flaggedByUsers.size());
	}
	
	public int getFlagsCount() {
		return this.flagsCount;
	}
	
	protected void setFlagsCount(int count) {
		this.flagsCount = count;
	}

	@Enumerated(EnumType.STRING)
    public int getUserThouchFlagStatus(User user) {
    	if (this.touchedUser(user)) {
    		return UserTouchFlagStatus.USER_TOUCHED_STATUS.value();
    	}
    	if (this.flaggedByUser(user)) {
    		return UserTouchFlagStatus.USER_FLAGGED_STATUS.value();
    	}
    	return UserTouchFlagStatus.USER_NONE_STATUS.value();
    }

	/**
	 * Two Gifts will generate the same hashcode if they have exactly the same
	 * values for their attributes
	 * 
	 */
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(this.title, this.text, this.content, this.thumbnail, this.createdBy.getUsername(), this.creationTimestamp, this.giftChain.getId());
	}

	/**
	 * Two Gifts are considered equal if they have exactly the same values for
	 * their attributes.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Gift) {
			Gift other = (Gift) obj;
			// Google Guava provides great utilities for equals too!
			return (this.id == other.id)
					&& Objects.equal(this.title, other.title)
					&& Objects.equal(this.text, other.text)
					&& Objects.equal(this.content, other.content)
					&& Objects.equal(this.thumbnail, other.thumbnail)
					&& Objects.equal(this.createdBy.getUsername(), other.createdBy.getUsername())
					&& (this.creationTimestamp == other.creationTimestamp)
					&& (this.giftChain.getId() == other.giftChain.getId())
					&& Objects.equal(this.touchedUsers, other.touchedUsers)
					&& Objects.equal(this.flaggedByUsers, other.flaggedByUsers);
		}
		else {
			return false;
		}
	}
}

package org.coursera.androidcapstone.server.repository;

import org.coursera.androidcapstone.common.repository.Gift;
import org.coursera.androidcapstone.common.client.GiftItem;
import org.coursera.androidcapstone.common.client.GiftDetail;
import org.coursera.androidcapstone.common.client.GiftCounters;
import org.coursera.androidcapstone.common.client.GiftSvcApi;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * An interface for a repository that can store Gift
 * objects and allow them to be searched by title and
 * using other search criteria.
 */

@Repository
public interface GiftRepository extends CrudRepository<Gift, Long> {
	// Find Gifts that have a specific title
	@Query("SELECT g FROM Gift g WHERE (g.title = :title)")
	public Collection<Gift> findByTitle(@Param(GiftSvcApi.TITLE_PARAMETER) String title);

	// Find all gifts that belongs to a specific username
	@Query("SELECT g FROM Gift g WHERE (g.createdBy.username = :username)")
	public Collection<Gift> findByUsername(@Param(GiftSvcApi.USERNAME_PARAMETER) String username);

    // Find all gifts with a matching GiftChain (e.g., Gift.giftChain)
    @Query("SELECT g FROM Gift g WHERE (g.giftChain.id = :id) ORDER BY g.creationTimestamp ASC")
    public Collection<Gift> findGiftChainById(@Param(GiftSvcApi.ID_PARAMETER) long id);

    @Query("SELECT NEW org.coursera.androidcapstone.common.client.GiftItem(g.id, g.title, g.thumbnail) FROM Gift g WHERE ((g.id = g.giftChain.id) AND ((:filter_flagged = FALSE) OR ((:filter_flagged = TRUE) AND (g.flagsCount = 0)))) ORDER BY g.title ASC")
    public Collection<GiftItem> findGiftChainsItems(@Param(GiftSvcApi.FILTER_FLAGGED_PARAMETER) boolean filter_flagged);

    @Query("SELECT NEW org.coursera.androidcapstone.common.client.GiftItem(g.id, g.title, g.thumbnail) FROM Gift g WHERE ((g.giftChain.id = :id) AND ((:filter_flagged = FALSE) OR ((:filter_flagged = TRUE) AND (g.flagsCount = 0)))) ORDER BY g.creationTimestamp ASC")
    public Collection<GiftItem> findGiftChainItemsById(@Param(GiftSvcApi.ID_PARAMETER) long id,
            										   @Param(GiftSvcApi.FILTER_FLAGGED_PARAMETER) boolean filter_flagged);

    @Query("SELECT NEW org.coursera.androidcapstone.common.client.GiftItem(g.id, g.title, g.thumbnail) FROM Gift g WHERE ((g.id = g.giftChain.id) AND (g.title LIKE :title) AND ((:filter_flagged = FALSE) OR ((:filter_flagged = TRUE) AND (g.flagsCount = 0)))) ORDER BY g.title ASC")
    public Collection<GiftItem> findGiftChainByTitle(@Param(GiftSvcApi.TITLE_PARAMETER)String title,
                                                     @Param(GiftSvcApi.FILTER_FLAGGED_PARAMETER) boolean filter_flagged);

    @Query("SELECT NEW org.coursera.androidcapstone.common.client.GiftItem(g.id, g.title, g.thumbnail) FROM Gift g WHERE ((g.giftChain.id = :id) AND (g.title LIKE :title) AND ((:filter_flagged = FALSE) OR ((:filter_flagged = TRUE) AND (g.flagsCount = 0)))) ORDER BY g.title ASC")
    public Collection<GiftItem> findGiftInChainByTitle(@Param(GiftSvcApi.ID_PARAMETER) long id,
                                                       @Param(GiftSvcApi.TITLE_PARAMETER) String title,
                                                       @Param(GiftSvcApi.FILTER_FLAGGED_PARAMETER) boolean filter_flagged);

    @Query("SELECT NEW org.coursera.androidcapstone.common.client.GiftDetail(g.id, g.title, g.text, g.content, g.createdBy.username, g.creationTimestamp, g.touchesCount, g.flagsCount, :username IN (SELECT tu FROM g.touchedUsers tu), :username IN (SELECT fbu FROM g.flaggedByUsers fbu)) FROM Gift g WHERE (g.id = :id)")
    public GiftDetail getGiftDetailById(@Param(GiftSvcApi.ID_PARAMETER) long id, @Param(GiftSvcApi.USERNAME_PARAMETER) String username);

    @Query("SELECT NEW org.coursera.androidcapstone.common.client.GiftCounters(g.id, g.touchesCount, g.flagsCount) FROM Gift g WHERE (g.id = :id)")
    public GiftCounters getGiftCountersById(@Param(GiftSvcApi.ID_PARAMETER) long id);

    @Query("DELETE FROM Gift g WHERE (g.id = :id)")
    public void deleteGiftById(@Param(GiftSvcApi.ID_PARAMETER) long id);

    @Query("DELETE FROM Gift g WHERE (g.giftChain.id = :id)")
    public void deleteGiftChainById(@Param(GiftSvcApi.ID_PARAMETER) long id);
}

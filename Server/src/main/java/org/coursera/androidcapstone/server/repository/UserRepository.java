package org.coursera.androidcapstone.server.repository;

import org.coursera.androidcapstone.common.client.GiftSvcApi;
import org.coursera.androidcapstone.common.client.TopGiftGiver;
import org.coursera.androidcapstone.common.repository.User;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * An interface for a repository that can store User
 * objects and allow them to be searched by username
 */

@Repository
public interface UserRepository extends CrudRepository<User, String> {
	// Find User that have a specific username
	@Query("SELECT u FROM User u WHERE (u.username = :username)")
	public User findByUsername(@Param(GiftSvcApi.USERNAME_PARAMETER) String username);

    // Find users that have the highest rated Gifts
    @Query("SELECT NEW org.coursera.androidcapstone.common.client.TopGiftGiver(u.username, u.avatar, u.totalTouchesCount) FROM User u ORDER BY u.totalTouchesCount DESC, u.username ASC")
    public Collection<TopGiftGiver> findTopGiftGivers();
}
package org.coursera.androidcapstone.common.client;

import org.coursera.androidcapstone.common.repository.Gift;

import java.util.Collection;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * This interface defines an API for a GiftSvc. The
 * interface is used to provide a contract for client/server
 * interactions. The interface is annotated with Retrofit
 * annotations so that clients can automatically convert the
 * interface into a client capable of sending the appropriate
 * HTTP requests.
 * 
 * The HTTP API that you must implement so that this interface
 * will work:
 *
 * POST /oauth/token
 *    - The access point for the OAuth 2.0 Password Grant flow.
 *    - Clients should be able to submit a request with their username, password,
 *      client ID, and client secret, encoded as described in the OAuth lecture videos.
 *    - The client ID for the Retrofit adapter is "mobile" with an empty password.
 *
 * GET /gift
 *    - Returns the list of gifts that have been added to the
 *      server as JSON. The list of gifts should be persisted
 *      using Spring Data. The list of Gift objects should be able 
 *      to be unmarshalled by the client into a Collection<Gift>.
 *    - The return content-type should be application/json, which
 *      will be the default if you use @ResponseBody
 *
 * POST /gift
 *    - The gift metadata is provided as an application/json request
 *      body. The JSON should generate a valid instance of the 
 *      Gift class when deserialized by Spring's default 
 *      Jackson library.
 *    - Returns the JSON representation of the Gift object that
 *      was stored along with any updates to that object made by the server. 
 *    - A gift should not have any touches and flags when it is initially created.
 *
 * GET /gift/{id}/chain
 *    - Returns the gift chain whose head gift has Id data member equal to id parameter,
 *      i.e. all the gifts whose GiftChain data member has value equal to id parameter.
 *
 * GET /gift/{id}
 *    - Returns the gift (GiftDetail) with the given id or 404 Not Found if the gift is not found.
 *      
 * POST /gift/{id}/touchedby
 *    - Allows a user to indicate they were touched by a gift.
 *      Returns 200 OK on success, 404 Not Found if the gift is not found, or 400 Bad Request
 *      if the user has already indicated that he was touched by the gift.
 *    - The service should keep track of which users have indicated as touched by a
 *      gift and prevent a user from indicating this twice.
 *    - A user is only allowed to indicate a gift once. If a user tries to indicate a gift
 *      a second time, the operation should fail and return 400 Bad Request.
 *    - If a user previously flagged the gift as offensive or inappropriate, then when the
 *      user indicates the gift the previous flag is also deleted.
 *      
 * POST /gift/{id}/untouchedby
 *    - Allows a user to remove indication for a gift that he/she previously indicated.
 *      Returns 200 OK on success, 404 Not Found if the gift is not found, and a 400 Bad Request
 *      if the user has not previously indicated the specified gift.
 *       
 * GET /gift/{id}/touched
 *    - Returns a list of the string usernames of the users that have indicated the specified
 *      gift. If the gift is not found, a 404 Not Found error should be generated.
 * 
 * POST /gift/{id}/flagged
 *    - Allows a user to flag a gift as offensive or inappropriate.
 *      Returns 200 OK on success, 404 Not Found if the gift is not found, or 400 Bad Request
 *      if the user has already flagged the gift.
 *    - The service should keep track of which users have flagged the gift and prevent a user
 *      from indicating this twice.
 *    - A user is only allowed to flag a gift once. If a user tries to flag a gift
 *      a second time, the operation should fail and return 400 Bad Request.
 *    - If a user previously indicated that he was touched by the gift, then when the user
 *      flags the gift the previous indication is also deleted.
 *      
 * POST /gift/{id}/unflagged
 *    - Allows a user to remove flagging for a gift that he/she previously flagged.
 *      Returns 200 OK on success, 404 Not Found if the gift is not found, and a 400 Bad Request
 *      if the user has not previously flagged the specified gift.
 *       
 * GET /gift/{id}/flaggedby
 *    - Returns a list of the string usernames of the users that have flagged the specified
 *      gift as offensive or inappropriate. If the gift is not found, a 404 Not Found
 *      error should be generated.
 *
 * GET /gift/search/findByTitle?title={title}
 *    - Returns a list of gifts whose titles match the given parameter or an empty
 *      list if none are found.
 *     
 * GET /gift/search/findByUsername?username={username}
 *    - Returns a list of gifts that belongs to username or an empty list if none are found.	
 *
 * GET /gift/search/findTopGivers
 *    - Returns the list of top "Gift givers", i.e., those whose Gifts have touched the most people.
 */
public interface GiftSvcApi {
    public static final String TOKEN_PATH = "/oauth/token";

	public static final String ID_PARAMETER = "id";
	public static final String CHAIN_ID_PARAMETER = "chain_id";
	public static final String TITLE_PARAMETER = "title";
	public static final String USERNAME_PARAMETER = "username";
    public static final String FILTER_FLAGGED_PARAMETER = "filter_flagged";

	// The path where we expect the GiftSvc to live
	public static final String GIFT_SVC_PATH = "/gift";
    public static final String GIFT_USERNAME_PATH = GiftSvcApi.GIFT_SVC_PATH + "/username";
    public static final String GIFT_ITEM_CHAIN_PATH = GiftSvcApi.GIFT_SVC_PATH + "/chain";
	public static final String GIFT_ID_PATH = GiftSvcApi.GIFT_SVC_PATH + "/{id}";
    public static final String GIFT_DETAIL_ID_PATH = GiftSvcApi.GIFT_SVC_PATH + "/{id}/detail";
    public static final String GIFT_COUNTERS_ID_PATH = GiftSvcApi.GIFT_SVC_PATH + "/{id}/counters";
    public static final String GIFT_CHAIN_ID_PATH = GiftSvcApi.GIFT_SVC_PATH + "/{id}/chain";
	public static final String GIFT_TOUCHEDBY_PATH = GiftSvcApi.GIFT_SVC_PATH + "/{id}/touchedby";
	public static final String GIFT_UNTOUCHEDBY_PATH = GiftSvcApi.GIFT_SVC_PATH + "/{id}/untouchedby";
	public static final String GIFT_TOUCHED_PATH = GiftSvcApi.GIFT_SVC_PATH + "/{id}/touched";
	public static final String GIFT_FLAGGED_PATH = GiftSvcApi.GIFT_SVC_PATH + "/{id}/flagged";
	public static final String GIFT_UNFLAGGED_PATH = GiftSvcApi.GIFT_SVC_PATH + "/{id}/unflagged";
	public static final String GIFT_FLAGGEDBY_PATH = GiftSvcApi.GIFT_SVC_PATH + "/{id}/flaggedby";

	// The path to search gifts by title
	public static final String GIFT_TITLE_SEARCH_PATH = GIFT_SVC_PATH + "/search/findByTitle";
    public static final String GIFT_CHAIN_TITLE_SEARCH_PATH = GIFT_SVC_PATH + "/chain/search/findByTitle";
    public static final String GIFT_IN_CHAIN_TITLE_SEARCH_PATH = GIFT_SVC_PATH + "/{id}/chain/search/findByTitle";
	// The path to search gifts by username
	public static final String GIFT_USERNAME_SEARCH_PATH = GIFT_SVC_PATH + "/search/findByUsername";
	// Find users that have the highest rated gifts
	public static final String GIFT_TOP_GIVERS_PATH = GIFT_SVC_PATH + "/search/findTopGivers";

	@GET(GIFT_SVC_PATH)
	public Collection<Gift> getGiftsList();

    @GET(GIFT_USERNAME_PATH)
    public String checkUsername();

    @GET(GIFT_ITEM_CHAIN_PATH)
    public Collection<GiftItem> getGiftChainsList(@Query(FILTER_FLAGGED_PARAMETER) boolean filter_flagged);

    @GET(GIFT_CHAIN_ID_PATH)
    public Collection<GiftItem> getGiftChainById(@Path(ID_PARAMETER) long id,
                                                 @Query(FILTER_FLAGGED_PARAMETER) boolean filter_flagged);

    @DELETE(GIFT_CHAIN_ID_PATH)
    public Void deleteGiftChainById(@Path(ID_PARAMETER) long id);

	@GET(GIFT_ID_PATH)
	public Gift getGiftById(@Path(ID_PARAMETER) long id);

    @GET(GIFT_DETAIL_ID_PATH)
    public GiftDetail getGiftDetailById(@Path(ID_PARAMETER) long id);

    @GET(GIFT_COUNTERS_ID_PATH)
    public GiftCounters getGiftCountersById(@Path(ID_PARAMETER) long id);

    @DELETE(GIFT_ID_PATH)
    public Void deleteGiftById(@Path(ID_PARAMETER) long id);

	@POST(GIFT_SVC_PATH)
	public Gift addGift(@Body Gift gift);

	@POST(GIFT_ID_PATH)
	public Gift addGiftToChain(@Path(ID_PARAMETER) long id, @Body Gift gift);

	@POST(GIFT_TOUCHEDBY_PATH)
	public Void touchedByGift(@Path(ID_PARAMETER) long id);

	@POST(GIFT_UNTOUCHEDBY_PATH)
	public Void untouchedByGift(@Path(ID_PARAMETER) long id);

	@GET(GIFT_TOUCHED_PATH)
	public Collection<String> getUsersTouchedByGift(@Path(ID_PARAMETER) long id);

	@POST(GIFT_FLAGGED_PATH)
	public Void flagGift(@Path(ID_PARAMETER) long id);

	@POST(GIFT_UNFLAGGED_PATH)
	public Void unflagGift(@Path(ID_PARAMETER) long id);

	@GET(GIFT_FLAGGEDBY_PATH)
	public Collection<String> getUsersWhoFlaggedGift(@Path(ID_PARAMETER) long id);

	@GET(GIFT_TITLE_SEARCH_PATH)
	public Collection<Gift> findByTitle(@Query(TITLE_PARAMETER) String title);

    @GET(GIFT_CHAIN_TITLE_SEARCH_PATH)
    public Collection<GiftItem> findGiftChainByTitle(@Query(TITLE_PARAMETER) String title,
                                                     @Query(FILTER_FLAGGED_PARAMETER) boolean filter_flagged);

    @GET(GIFT_IN_CHAIN_TITLE_SEARCH_PATH)
    public Collection<GiftItem> findGiftInChainByTitle(@Path(ID_PARAMETER) long id,
                                                       @Query(TITLE_PARAMETER) String title,
                                                       @Query(FILTER_FLAGGED_PARAMETER) boolean filter_flagged);

	@GET(GIFT_USERNAME_SEARCH_PATH)
	public Collection<Gift> findByUsername(@Query(USERNAME_PARAMETER) String username);

	@GET(GIFT_TOP_GIVERS_PATH)
	public Collection<TopGiftGiver> findTopGiftGivers();
}

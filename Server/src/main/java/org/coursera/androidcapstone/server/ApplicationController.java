package org.coursera.androidcapstone.server;

import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.coursera.androidcapstone.common.client.GiftItem;
import org.coursera.androidcapstone.common.client.GiftDetail;
import org.coursera.androidcapstone.common.client.GiftCounters;
import org.coursera.androidcapstone.common.client.GiftSvcApi;
import org.coursera.androidcapstone.common.client.TopGiftGiver;
import org.coursera.androidcapstone.common.repository.Gift;
import org.coursera.androidcapstone.common.repository.User;
import org.coursera.androidcapstone.server.repository.GiftRepository;
import org.coursera.androidcapstone.server.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;

@Controller
public class ApplicationController {

	// The GiftRepository that we are going to store our gifts
	// in. We don't explicitly construct a GiftRepository, but
	// instead mark this object as a dependency that needs to be
	// injected by Spring. Our Application class has a method
	// annotated with @Bean that determines what object will end
	// up being injected into this member variable.
	// Also notice that we don't even need a setter for Spring to
	// do the injection.

	@Autowired
	private GiftRepository gifts;

	@Autowired
	private UserRepository users;
	
	@RequestMapping(value=GiftSvcApi.GIFT_SVC_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Gift> getGiftsList() {
		return Lists.newArrayList(gifts.findAll());
	}

    @RequestMapping(value=GiftSvcApi.GIFT_USERNAME_PATH, method=RequestMethod.GET)
    public @ResponseBody String checkUsername(HttpServletResponse response,
                                			  Principal p) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        String username = p.getName();
        User user = users.findByUsername(username);
        if (user != null) {
            response.setStatus(HttpStatus.OK.value());
            return user.getUsername();
        }
        response.setStatus(httpStatus.value());
        if (response.getStatus() != HttpStatus.OK.value()) {
            try {
                response.sendError(httpStatus.value(), httpStatus.getReasonPhrase());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    @RequestMapping(value=GiftSvcApi.GIFT_ITEM_CHAIN_PATH, method=RequestMethod.GET)
    public @ResponseBody Collection<GiftItem> getGiftChainsList(@RequestParam(GiftSvcApi.FILTER_FLAGGED_PARAMETER) boolean filter_flagged) {
        return Lists.newArrayList(gifts.findGiftChainsItems(filter_flagged));
    }

    @RequestMapping(value=GiftSvcApi.GIFT_CHAIN_ID_PATH, method=RequestMethod.GET)
    public @ResponseBody Collection<GiftItem> getGiftChainById(@PathVariable(GiftSvcApi.ID_PARAMETER) long id,
                                                               @RequestParam(GiftSvcApi.FILTER_FLAGGED_PARAMETER) boolean filter_flagged,
                                                               HttpServletResponse response) {
        Collection<GiftItem> giftChain = gifts.findGiftChainItemsById(id, filter_flagged);
        if (giftChain != null) {
            response.setStatus(HttpStatus.OK.value());
            return Lists.newArrayList(giftChain);
        }
        response.setStatus(HttpStatus.NOT_FOUND.value());
        try {
            response.sendError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value=GiftSvcApi.GIFT_CHAIN_ID_PATH, method=RequestMethod.DELETE)
    public @ResponseBody HttpServletResponse deleteGiftChainById(@PathVariable(GiftSvcApi.ID_PARAMETER) long id,
                                                                 HttpServletResponse response,
                                                                 Principal p) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        Gift gift = gifts.findOne(id);
        if (gift != null) {
            if (gift.getId() == gift.getGiftChain().getId()) {
                String username = p.getName();
                User user = users.findByUsername(username);
                if (user != null) {
                    boolean canBeDeleted = true;
                    long deletedCount = 0;
                    for (Gift g : gifts.findGiftChainById(id)) {
                        if (!g.getCreatedBy().getUsername().equals(username)) {
                            canBeDeleted = false;
                            break;
                        }
                        deletedCount += g.getTouchesCount();
                    }
                    if (canBeDeleted) {
                        gifts.deleteGiftChainById(id);
                        long ttc = user.getTotalTouchesCount();
                        ttc -= deletedCount;
                        user.setTotalTouchesCount(ttc);
                        users.save(user);
                        httpStatus = HttpStatus.OK;
                    }
                    else {
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                }
            }
        }
        response.setStatus(httpStatus.value());
        if (response.getStatus() != HttpStatus.OK.value()) {
            try {
                response.sendError(httpStatus.value(), httpStatus.getReasonPhrase());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
	@RequestMapping(value=GiftSvcApi.GIFT_ID_PATH, method=RequestMethod.GET)
	public @ResponseBody Gift getGiftById(@PathVariable(GiftSvcApi.ID_PARAMETER) long id,
                                          HttpServletResponse response) {
		Gift gift = gifts.findOne(id);
		if (gift != null) {
			response.setStatus(HttpStatus.OK.value());
			return gift;
		}
		response.setStatus(HttpStatus.NOT_FOUND.value());
		try {
			response.sendError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

    @RequestMapping(value=GiftSvcApi.GIFT_DETAIL_ID_PATH, method=RequestMethod.GET)
    public @ResponseBody GiftDetail getGiftDetailById(@PathVariable(GiftSvcApi.ID_PARAMETER) long id,
                                                      HttpServletResponse response,
                                                      Principal p) {
    	String username = p.getName();
		User user = users.findByUsername(username);
		if (user != null) {
			GiftDetail giftDetail = gifts.getGiftDetailById(id, user.getUsername());
			if (giftDetail != null) {
				response.setStatus(HttpStatus.OK.value());
				return giftDetail;
			}
        }
        response.setStatus(HttpStatus.NOT_FOUND.value());
        try {
            response.sendError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value=GiftSvcApi.GIFT_COUNTERS_ID_PATH, method=RequestMethod.GET)
    public @ResponseBody GiftCounters getGiftCountersById(@PathVariable(GiftSvcApi.ID_PARAMETER) long id,
                                                          HttpServletResponse response) {
        GiftCounters giftCounters = gifts.getGiftCountersById(id);
        if (giftCounters != null) {
            response.setStatus(HttpStatus.OK.value());
            return giftCounters;
        }
        response.setStatus(HttpStatus.NOT_FOUND.value());
        try {
            response.sendError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @RequestMapping(value=GiftSvcApi.GIFT_SVC_PATH, method=RequestMethod.DELETE)
    public @ResponseBody HttpServletResponse deleteGiftById(@PathVariable(GiftSvcApi.ID_PARAMETER) long id,
                                                            HttpServletResponse response,
                                                            Principal p) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        Gift gift = gifts.findOne(id);
        if (gift != null) {
            String username = p.getName();
            User user = users.findByUsername(username);
            if (user != null) {
                if (gift.getCreatedBy().getUsername().equals(username)) {
                    int tc = gift.getTouchesCount();
                    gifts.deleteGiftById(id);
                    long ttc = user.getTotalTouchesCount();
                    ttc -= tc;
                    user.setTotalTouchesCount(ttc);
                    users.save(user);
                    httpStatus = HttpStatus.OK;
                }
                else {
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            }
        }
        response.setStatus(httpStatus.value());
        if (response.getStatus() != HttpStatus.OK.value()) {
            try {
                response.sendError(httpStatus.value(), httpStatus.getReasonPhrase());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

	@RequestMapping(value=GiftSvcApi.GIFT_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody Gift addGift(@RequestBody Gift gift,
	                                  HttpServletResponse response,
									  Principal p) {
		Gift savedGift = gift;
		HttpStatus httpStatus = HttpStatus.NOT_FOUND;
		String username = p.getName();
		User user = users.findByUsername(username);
		if (user != null) {
			gift.setCreatedBy(user);
			gift.setCreationTimestamp(Instant.now().toEpochMilli());
			gift.setGiftChain(gift);
			savedGift = gifts.save(gift);
            users.save(user);
			httpStatus = HttpStatus.OK;
		}
		else {
			httpStatus = HttpStatus.BAD_REQUEST;
			savedGift = null;
		}
		response.setStatus(httpStatus.value());
		if (response.getStatus() != HttpStatus.OK.value()) {
			try {
				response.sendError(httpStatus.value(), httpStatus.getReasonPhrase());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return savedGift;
	}

	@RequestMapping(value=GiftSvcApi.GIFT_ID_PATH, method=RequestMethod.POST)
	public @ResponseBody Gift addGiftToChain(@PathVariable(GiftSvcApi.ID_PARAMETER) long id,
											 @RequestBody Gift gift,
											 HttpServletResponse response,
											 Principal p) {		
		Gift savedGift = gift;
		HttpStatus httpStatus = HttpStatus.NOT_FOUND;
		String username = p.getName();
		User user = users.findByUsername(username);
		if (user != null) {
			Gift giftChain = gifts.findOne(id);
			if (giftChain != null) {
				gift.setCreatedBy(user);
				gift.setCreationTimestamp(Instant.now().toEpochMilli());
				gift.setGiftChain(giftChain);
				savedGift = gifts.save(gift);
                users.save(user);
				httpStatus = HttpStatus.OK;
			}
			else {
				savedGift = null;
			}
		}
		else {
			httpStatus = HttpStatus.BAD_REQUEST;
			savedGift = null;
		}
		response.setStatus(httpStatus.value());
		if (response.getStatus() != HttpStatus.OK.value()) {
			try {
				response.sendError(httpStatus.value(), httpStatus.getReasonPhrase());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return savedGift;
	}

	@RequestMapping(value=GiftSvcApi.GIFT_TOUCHEDBY_PATH, method=RequestMethod.POST)
	public @ResponseBody HttpServletResponse touchedByGift(@PathVariable(GiftSvcApi.ID_PARAMETER) long id,
                                                           HttpServletResponse response,
                                                           Principal p) {
		HttpStatus httpStatus = HttpStatus.NOT_FOUND;
		Gift gift = gifts.findOne(id);
		if (gift != null) {
			String username = p.getName();
			User user = users.findByUsername(username);
			if (user != null) {
				if (gift.getCreatedBy().getUsername().equals(username)) {
					httpStatus = HttpStatus.BAD_REQUEST;
				}
				else {
					if (gift.touchedUser(user)) {
						httpStatus = HttpStatus.BAD_REQUEST;
					}
					else {
						gift.addTouchedUser(user);
						gifts.save(gift);
						users.save(gift.getCreatedBy());
                        users.save(user);
						httpStatus = HttpStatus.OK;
					}
				}
			}
		}
		response.setStatus(httpStatus.value());
		if (response.getStatus() != HttpStatus.OK.value()) {
			try {
				response.sendError(httpStatus.value(), httpStatus.getReasonPhrase());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@RequestMapping(value=GiftSvcApi.GIFT_UNTOUCHEDBY_PATH, method=RequestMethod.POST)
	public @ResponseBody HttpServletResponse untouchedByGift(@PathVariable(GiftSvcApi.ID_PARAMETER) long id,
                                                             HttpServletResponse response,
                                                             Principal p) {
		HttpStatus httpStatus = HttpStatus.NOT_FOUND;
		Gift gift = gifts.findOne(id);
		if (gift != null) {
			String username = p.getName();
			User user = users.findByUsername(username);
			if (user != null) {
				if (gift.getCreatedBy().getUsername().equals(username)) {
					httpStatus = HttpStatus.BAD_REQUEST;
				}
				else {
					if (gift.touchedUser(user)) {
						gift.removeTouchedUser(user);
						gifts.save(gift);
						users.save(gift.getCreatedBy());
                        users.save(user);
						httpStatus = HttpStatus.OK;
					}
					else {
						httpStatus = HttpStatus.BAD_REQUEST;
					}
				}
			}
		}
		response.setStatus(httpStatus.value());
		if (response.getStatus() != HttpStatus.OK.value()) {
			try {
				response.sendError(httpStatus.value(), httpStatus.getReasonPhrase());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@RequestMapping(value=GiftSvcApi.GIFT_TOUCHED_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<String> getUsersTouchedByGift(@PathVariable(GiftSvcApi.ID_PARAMETER) long id,
                                                                  HttpServletResponse response) {
		Gift gift = gifts.findOne(id);
		if (gift != null) {
			response.setStatus(HttpStatus.OK.value());
			return gift.touchedUsersAsList();
		}
		response.setStatus(HttpStatus.NOT_FOUND.value());
		try {
			response.sendError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value=GiftSvcApi.GIFT_FLAGGED_PATH, method=RequestMethod.POST)
	public @ResponseBody HttpServletResponse flagGift(@PathVariable(GiftSvcApi.ID_PARAMETER) long id,
                         							  HttpServletResponse response,
                         							  Principal p) {
		HttpStatus httpStatus = HttpStatus.NOT_FOUND;
		Gift gift = gifts.findOne(id);
		if (gift != null) {
			String username = p.getName();
			User user = users.findByUsername(username);
			if (user != null) {
				if (gift.getCreatedBy().getUsername().equals(username)) {
					httpStatus = HttpStatus.BAD_REQUEST;
				}
				else {
					if (gift.flaggedByUser(user)) {
						httpStatus = HttpStatus.BAD_REQUEST;
					}
					else {
						gift.addUserFlag(user);
						gifts.save(gift);
						users.save(gift.getCreatedBy());
                        users.save(user);
						httpStatus = HttpStatus.OK;
					}
				}
			}
		}
		response.setStatus(httpStatus.value());
		if (response.getStatus() != HttpStatus.OK.value()) {
			try {
				response.sendError(httpStatus.value(), httpStatus.getReasonPhrase());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@RequestMapping(value=GiftSvcApi.GIFT_UNFLAGGED_PATH, method=RequestMethod.POST)
	public @ResponseBody HttpServletResponse unflagGift(@PathVariable(GiftSvcApi.ID_PARAMETER) long id,
                           								HttpServletResponse response,
                           								Principal p) {
		HttpStatus httpStatus = HttpStatus.NOT_FOUND;
		Gift gift = gifts.findOne(id);
		if (gift != null) {
			String username = p.getName();
			User user = users.findByUsername(username);
			if (user != null) {
				if (gift.getCreatedBy().getUsername().equals(username)) {
					httpStatus = HttpStatus.BAD_REQUEST;
				}
				else {
					if (gift.flaggedByUser(user)) {
						gift.removeUserFlag(user);
						gifts.save(gift);
						users.save(gift.getCreatedBy());
                        users.save(user);
						httpStatus = HttpStatus.OK;
					}
					else {
						httpStatus = HttpStatus.BAD_REQUEST;
					}
				}
			}
		}
		response.setStatus(httpStatus.value());
		if (response.getStatus() != HttpStatus.OK.value()) {
			try {
				response.sendError(httpStatus.value(), httpStatus.getReasonPhrase());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@RequestMapping(value=GiftSvcApi.GIFT_FLAGGEDBY_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<String> getUsersWhoFlaggedGift(@PathVariable(GiftSvcApi.ID_PARAMETER) long id,
                                                                   HttpServletResponse response) {
		Gift gift = gifts.findOne(id);
		if (gift != null) {
			response.setStatus(HttpStatus.OK.value());
			return gift.flaggedByUsersAsList();
		}
		response.setStatus(HttpStatus.NOT_FOUND.value());
		try {
			response.sendError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value=GiftSvcApi.GIFT_TITLE_SEARCH_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Gift> findByTitle(@RequestParam(GiftSvcApi.TITLE_PARAMETER) String title) {
		return Lists.newArrayList(gifts.findByTitle(title));
	}

    @RequestMapping(value=GiftSvcApi.GIFT_CHAIN_TITLE_SEARCH_PATH, method=RequestMethod.GET)
    public @ResponseBody Collection<GiftItem> findGiftChainByTitle(@RequestParam(GiftSvcApi.TITLE_PARAMETER) String title,
                                                                   @RequestParam(GiftSvcApi.FILTER_FLAGGED_PARAMETER) boolean filter_flagged) {
        return Lists.newArrayList(gifts.findGiftChainByTitle(title, filter_flagged));
    }

    @RequestMapping(value=GiftSvcApi.GIFT_IN_CHAIN_TITLE_SEARCH_PATH, method=RequestMethod.GET)
    public @ResponseBody Collection<GiftItem> findGiftInChainByTitle(@PathVariable(GiftSvcApi.ID_PARAMETER) long id,
                                                                     @RequestParam(GiftSvcApi.TITLE_PARAMETER) String title,
                                                                     @RequestParam(GiftSvcApi.FILTER_FLAGGED_PARAMETER) boolean filter_flagged) {
        return Lists.newArrayList(gifts.findGiftInChainByTitle(id, title, filter_flagged));
    }
    
	@RequestMapping(value=GiftSvcApi.GIFT_USERNAME_SEARCH_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Gift> findByUsername(@RequestParam(GiftSvcApi.USERNAME_PARAMETER) String username) {
		return Lists.newArrayList(gifts.findByUsername(username));
	}
	
	@RequestMapping(value=GiftSvcApi.GIFT_TOP_GIVERS_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<TopGiftGiver> findTopGiftGivers() {
		return Lists.newArrayList(users.findTopGiftGivers());
	}
}

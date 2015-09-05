package org.coursera.androidcapstone.server.oauth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;

/**
 * A class that combines a UserDetailsService and ClientDetailsService
 * into a single object.
 * 
 * @author jules
 *
 */
public class ClientAndUserDetailsService implements UserDetailsService, ClientDetailsService {
	private final ClientDetailsService clients;
	private final UserDetailsService users;
	private final ClientDetailsUserDetailsService clientDetailsWrapper;

	public ClientAndUserDetailsService(ClientDetailsService clients, UserDetailsService users) {
		super();
		this.clients = clients;
		this.users = users;
		this.clientDetailsWrapper = new ClientDetailsUserDetailsService(clients);
	}

	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		return this.clients.loadClientByClientId(clientId);
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails user = null;
		try {
			user = this.users.loadUserByUsername(username);
		}
		catch (UsernameNotFoundException e) {
			user = this.clientDetailsWrapper.loadUserByUsername(username);
		}
		return user;
	}
}

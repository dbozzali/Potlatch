package org.coursera.androidcapstone.server.oauth;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

public class UserSecurity implements UserDetails {
	private static final long serialVersionUID = -7911209679890839062L;

	public static UserDetails create(String username, String password, String...authorities) {
		return new UserSecurity(username, password, authorities);
	}
	
	private final Collection<GrantedAuthority> authorities;
	private final String password;
	private final String username;

	@SuppressWarnings("unchecked")
	private UserSecurity(String username, String password) {
		this(username, password, Collections.EMPTY_LIST);
	}

	private UserSecurity(String username, String password, String...authorities) {
		this.username = username;
		this.password = password;
		this.authorities = AuthorityUtils.createAuthorityList(authorities);
	}

	private UserSecurity(String username, String password, Collection<GrantedAuthority> authorities) {
		super();
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}

	public Collection<GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	public String getPassword() {
		return this.password;
	}

	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}

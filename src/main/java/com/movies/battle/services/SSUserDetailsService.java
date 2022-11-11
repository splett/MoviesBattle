package com.movies.battle.services;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.movies.battle.model.Role;
import com.movies.battle.model.User;
import com.movies.battle.repository.UserRepository;

@Transactional
@Service
public class SSUserDetailsService implements UserDetailsService {

	private UserRepository userRepository;

	public SSUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Locates the user based on the username.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			User user = userRepository.findByUsername(username);
			if (user == null) {
				return null;
			}
			return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
					getAuthories(user));
		} catch (Exception e) {
			throw new UsernameNotFoundException("User not found!");
		}
	}

	/**
	 * Checks the authorities saved for the user
	 * 
	 * @param user {@link User}
	 * @return {@link Set} with the granted authority
	 */
	private Set<GrantedAuthority> getAuthories(User user) {

		Set<GrantedAuthority> authorities = new HashSet<>();
		for (Role role : user.getRoles()) {
			GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getRole());
			authorities.add(grantedAuthority);
		}
		return authorities;
	}
}

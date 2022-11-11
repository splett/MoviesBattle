package com.movies.battle;

import com.movies.battle.repository.UserRepository;
import com.movies.battle.services.SSUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetailsService userDetailsServiceBean() throws Exception {
		return new SSUserDetailsService(userRepository);
	}

	
	/**
	 * Performs permission configuration for users and administrators
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/", "/h2-console/**").permitAll()
			.antMatchers("/admin").access("hasAuthority('ADMIN')")
			.antMatchers("/addNewMovies").access("hasAuthority('ADMIN')")
			.antMatchers("/signup").permitAll()
			.antMatchers("/swagger-ui.html").permitAll()
			.anyRequest().authenticated()
			.and().formLogin().loginPage("/login").permitAll().and().logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login").permitAll().and()
			.httpBasic();
		http.csrf().disable();
		http.headers().frameOptions().disable();
	}

	/**
	 * Encrypts the user's password
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsServiceBean()).passwordEncoder(passwordEncoder());
	}

	/**
	 * Generates an object used to encode information
	 * 
	 * @return {@link BCryptPasswordEncoder} BCryptPasswordEncoder
	 */
	@Bean
	public static BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

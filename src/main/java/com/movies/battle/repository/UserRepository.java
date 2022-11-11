package com.movies.battle.repository;

import org.springframework.data.repository.CrudRepository;

import com.movies.battle.model.User;

public interface UserRepository extends CrudRepository <User, Long> {
	public User findByUsername(String username);
}

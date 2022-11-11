package com.movies.battle.repository;

import org.springframework.data.repository.CrudRepository;

import com.movies.battle.model.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
	public Role findByRole(String role);
}

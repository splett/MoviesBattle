package com.movies.battle.repository;

import org.springframework.data.repository.CrudRepository;

import com.movies.battle.model.Game;
import com.movies.battle.model.User;

public interface GameRepository extends CrudRepository<Game, Long>{

	public Game findByUserAndCurrentGame(User user, boolean currentGame);
}

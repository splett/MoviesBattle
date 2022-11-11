package com.movies.battle.repository;

import org.springframework.data.repository.CrudRepository;

import com.movies.battle.model.Movie;

public interface MovieRepository extends CrudRepository<Movie, Long> {
	public Movie findByImdbID(String imdbID);
	public Movie findById(long id);
}

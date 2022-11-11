package com.movies.battle.controllers.movie;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.movies.battle.model.Movie;
import com.movies.battle.repository.MovieRepository;

@RestController
public class MovieController {

	@Autowired
	private MovieRepository movieRepository;

	/**
	 * Performs the search of random movie's information on OMDBApi and saves on
	 * repository.
	 * 
	 * @param qtd {@link Integer} number of films to add
	 * @return {@link ResponseEntity} with the new movies added
	 */
	@GetMapping("/admin/addNewMovies/{qtd}")
	public ResponseEntity<Object> toAddNewMovies(@PathVariable("qtd") int qtd) {
		MoviesLoader moviesLoader = new MoviesLoader();
		int moviesAdded = 0;
		List<Movie> movies = new ArrayList<>();

		while (moviesAdded < qtd) {
			Movie movie = moviesLoader.findMovieOnOMDBApi();
			if (moviesLoader.movieIsValid(movie)) {
				movieRepository.save(movie);
				movies.add(movie);
				moviesAdded++;
			}
		}

		return ResponseEntity.ok().body(movies);
	}
}

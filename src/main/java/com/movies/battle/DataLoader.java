package com.movies.battle;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.movies.battle.controllers.movie.MoviesLoader;
import com.movies.battle.model.Movie;
import com.movies.battle.model.Role;
import com.movies.battle.model.User;
import com.movies.battle.repository.MovieRepository;
import com.movies.battle.repository.RoleRepository;
import com.movies.battle.repository.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	// Number of films to load
	private static int MOVIES_COUNT = 30;

	/**
	 * Performs data loading for the application
	 */
	@Override
	public void run(String... args) throws Exception {
		createRoles();
		createUserAdmin();
		createMovies();
		createPlayer();
	}

	/**
	 * Performs the search of movie's information on OMDBApi and saves on
	 * repository.
	 */
	private void createMovies() {
		MoviesLoader moviesLoader = new MoviesLoader();
		
		int moviesAdded = 0;
		
		while (moviesAdded < MOVIES_COUNT) {
			Movie movie = moviesLoader.findMovieOnOMDBApi();
			if (moviesLoader.movieIsValid(movie)) {
				movieRepository.save(movie);
				moviesAdded++;
			}
		}

		/*
		 * Used for testing because OMDBApi has a limit of 1000 daily requests.
		 * movieRepository.save(new Movie("1", "Title 1", 10F, 500L));
		 * movieRepository.save(new Movie("2", "Title 2", 5F, 500L));
		 * movieRepository.save(new Movie("3", "Title 3", 2F, 500L));
		 * movieRepository.save(new Movie("4", "Title 4", 6F, 500L));
		 * movieRepository.save(new Movie("5", "Title 5", 7F, 500L));
		 * movieRepository.save(new Movie("6", "Title 6", 8.5F, 500L));
		 * movieRepository.save(new Movie("7", "Title 7", 9F, 500L));
		 * movieRepository.save(new Movie("8", "Title 8", 4.5F, 500L));
		 */
	}

	/**
	 * Creates User and Admin roles
	 */
	private void createRoles() {
		roleRepository.save(new Role("USER"));
		roleRepository.save(new Role("ADMIN"));
	}

	/**
	 * Creates user Administrator
	 */
	private void createUserAdmin() {
		Role adminRole = roleRepository.findByRole("ADMIN");

		User user = new User("Admin", "admin@code.com", "admin", passwordEncoder.encode("password"));
		user.setRoles(Arrays.asList(adminRole));
		userRepository.save(user);
	}
	
	/**
	 * Creates a normal player
	 */
	private void createPlayer() {
		Role adminRole = roleRepository.findByRole("USER");

		User user = new User("Player 1", "player1@code.com", "player", passwordEncoder.encode("password"));
		user.setRoles(Arrays.asList(adminRole));
		userRepository.save(user);
	}
}

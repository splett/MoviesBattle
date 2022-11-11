package com.movies.battle.controllers.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movies.battle.model.Game;
import com.movies.battle.model.User;
import com.movies.battle.repository.GameRepository;
import com.movies.battle.repository.UserRepository;

@RestController
public class GameController {

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private UserRepository userRepository;

	/**
	 * Starts a new game
	 * 
	 * @return {@link ResponseEntity}
	 */
	@GetMapping("/startGame")
	public ResponseEntity<Object> toStartGame() {

		User user = findCurrentUser();

		if (gameRepository.findByUserAndCurrentGame(user, true) != null) {
			return ResponseEntity.ok("There are a current game running. Finish it or end.");
		} else {
			toSaveNewGame();
			return ResponseEntity.ok("Game Started");
		}
	}

	/**
	 * Ends the current game
	 * 
	 * @return {@link ResponseEntity}
	 */
	@GetMapping("/endGame")
	public ResponseEntity<Object> toEndGame() {
		User user = findCurrentUser();
		Game currentGame = gameRepository.findByUserAndCurrentGame(user, true);

		if (gameRepository.findByUserAndCurrentGame(user, true) != null) {
			toSaveEndGame(currentGame);
			return ResponseEntity.ok("Game finished. Total points: " + currentGame.getPoints());
		} else {
			return ResponseEntity.ok("There are no games running. ");
		}
	}

	/**
	 * End game for current user and saves
	 * 
	 * @param currentGame {@link Game}
	 */
	private void toSaveEndGame(Game currentGame) {
		currentGame.setCurrentGame(false);
		gameRepository.save(currentGame);
	}

	/**
	 * Saves a new game for current user
	 */
	private void toSaveNewGame() {
		Game game = new Game(findCurrentUser());
		gameRepository.save(game);
	}

	/**
	 * Finds the user logged in
	 * 
	 * @return {@link User} User logged in
	 */
	private User findCurrentUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userRepository.findByUsername(((UserDetails) principal).getUsername());
	}

}

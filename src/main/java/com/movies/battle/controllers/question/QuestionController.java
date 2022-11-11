package com.movies.battle.controllers.question;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.movies.battle.model.Game;
import com.movies.battle.model.Movie;
import com.movies.battle.model.Question;
import com.movies.battle.model.User;
import com.movies.battle.repository.GameRepository;
import com.movies.battle.repository.MovieRepository;
import com.movies.battle.repository.QuestionRepository;
import com.movies.battle.repository.UserRepository;

@RestController
public class QuestionController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private QuestionRepository questionRepository;

	private static int OPTION_1 = 1;
	private static int OPTION_2 = 2;

	/**
	 * Endpoint used to show the question to the player
	 * 
	 * @return {@link ResponseEntity} with current {@link Question} 
	 */
	@GetMapping("/question")
	public ResponseEntity<Object> getQuestion() {
		Game game = gameRepository.findByUserAndCurrentGame(toFindCurrentUser(), true);

		if (thereIsCurrentGame(game)) {
			if (game.getCurrentQuestionID() == null) {
				game.setCurrentQuestionID(toGenerateQuestionWithPairOfMovies(game).getId());
				gameRepository.save(game);
			}

			return ResponseEntity.ok().body(questionRepository.findById(game.getCurrentQuestionID()));
		} else
			return responseInformingThereIsNoCurrentGame();
	}

	/**
	 * Endpoint used to answer the current question
	 * 
	 * @param option {@link Integer}  choosen option
	 * @return {@link ResponseEntity} with messages 
	 */
	@GetMapping("/answer/{option}")
	public ResponseEntity<Object> toAnswerQuestion(@PathVariable("option") int option) {

		if (option != OPTION_1 && option != OPTION_2)
			return ResponseEntity.ok().body("Invalid Option. Choose 1 or 2. ");

		Game game = gameRepository.findByUserAndCurrentGame(toFindCurrentUser(), true);
		List<String> messages = new ArrayList<>();

		if (thereIsCurrentGame(game)) {
			Question question = questionRepository.findById(game.getCurrentQuestionID()).get();

			Movie movieOption1 = toFindMovieInCurrentQuestion(question, 0);
			Movie movieOption2 = toFindMovieInCurrentQuestion(question, 1);

			toAddInformationAboutMoviesInCurrentQuestion(messages, movieOption1, movieOption2);

			if (isCorrectAnswer(option, movieOption1, movieOption2))
				updateForCorrectAnswer(game, messages);
			else
				updateForWrongAnswer(game, messages);

			gameRepository.save(game);
			return ResponseEntity.ok().body(messages);
		} else
			return responseInformingThereIsNoCurrentGame();
	}

	/**
	 * Search for the information of the films present in the question
	 * 
	 * @param question {@link Question} current question
	 * @param index    {@link Integer} 
	 * @return {@link Movie} 
	 */
	private Movie toFindMovieInCurrentQuestion(Question question, int index) {
		return question.getMovies().get(index);
	}

	/**
	 * Returns a message informing that there is no current game
	 * 
	 * @return {@link ResponseEntity} with message
	 */
	private ResponseEntity<Object> responseInformingThereIsNoCurrentGame() {
		return ResponseEntity.ok("There are no games running. Try to start one!");
	}

	/**
	 * Checks if there is current game
	 * 
	 * @param game {@link Game} current game
	 * @return {@link Boolean} true if there is a current game
	 */
	private boolean thereIsCurrentGame(Game game) {
		return game != null;
	}

	/**
	 * Check if the player answered the correct movie
	 * 
	 * @param option       {@link Integer} Option chosen by the player
	 * @param movieOption1 {@link Movie} First movie option in the current question
	 * @param movieOption2 {@link Movie} Second movie option in the current question
	 * @return {@link Boolean} true if the answer is correct, otherwise, false.
	 */
	private boolean isCorrectAnswer(int option, Movie movieOption1, Movie movieOption2) {
		return option == OPTION_1 && movieOption1.getScore() > movieOption2.getScore()
				|| option == OPTION_2 && movieOption2.getScore() > movieOption1.getScore();
	}

	/**
	 * Adds information messages about the movies present in the current question
	 * 
	 * @param messages     {@link List} Message list to return
	 * @param movieOption1 {@link Movie} First movie option in the current question
	 * @param movieOption2 {@link Movie} Second movie option in the current question
	 */
	private void toAddInformationAboutMoviesInCurrentQuestion(List<String> messages, Movie movieOption1,
			Movie movieOption2) {
		toAddMessagesAboutMovie(messages, movieOption1);
		toAddMessagesAboutMovie(messages, movieOption2);
	}

	/**
	 * Update of information when the player gets the answer right
	 * 
	 * @param game     {@link Game} current game
	 * @param messages {@link List} message list
	 */
	private void updateForCorrectAnswer(Game game, List<String> messages) {
		game.setPoints(game.getPoints() + 1);
		messages.add("Answer is Correct!");
		toGenerateNewQuestion(game);
	}

	/**
	 * Update of information when the player gets the answer wrong
	 * 
	 * @param game     {@link Game} current game
	 * @param messages {@link List} message list
	 */
	private void updateForWrongAnswer(Game game, List<String> messages) {
		messages.add("Answer is Wrong!");
		game.setErrors(game.getErrors() + 1);
		if (game.getErrors() >= 3) {
			toEndGame(game, messages);
		} else
			toGenerateNewQuestion(game);
	}

	/**
	 * End the current game
	 * 
	 * @param game     {@link Game} current game
	 * @param messages {@link List} message list
	 */
	private void toEndGame(Game game, List<String> messages) {
		game.setCurrentGame(false);
		messages.add("Game Over!");
		messages.add("Total score: " + game.getPoints());
	}

	/**
	 * Add movie information in message list
	 * 
	 * @param messages {@link List} message list
	 * @param movie    {@link Movie} with information to add
	 */
	private void toAddMessagesAboutMovie(List<String> messages, Movie movie) {
		messages.add("Movie " + movie.getTitle() + ": " + movie.getScore());
	}

	/**
	 * Generate a new question and saves in the current question of the game
	 * 
	 * @param game {@link Game} current game
	 */
	private void toGenerateNewQuestion(Game game) {
		game.setCurrentQuestionID(toGenerateQuestionWithPairOfMovies(game).getId());
	}

	/**
	 * Generates a new question containing two movies
	 * 
	 * @param game {@link Game} current game
	 * @return {@link Question} a new question
	 */
	private Question toGenerateQuestionWithPairOfMovies(Game game) {
		Question question = new Question();
		toAddPairOfMoviesInQuestion(game, question);
		return questionRepository.save(question);
	}

	/**
	 * Add a couple of movies to the new question
	 * 
	 * @param game     {@link Game} current game
	 * @param question {@link Question} new question to add movies
	 */
	private void toAddPairOfMoviesInQuestion(Game game, Question question) {
		toAddMovieInQuestion(question, game);
		toAddMovieInQuestion(question, game);
	}

	/**
	 * Adds movies that were not answered in the current game in the question.
	 * 
	 * @param question {@link Question} new question to add movies
	 * @param game     {@link Game} current game
	 */
	private void toAddMovieInQuestion(Question question, Game game) {
		Movie movie = null;
		do {
			Integer radomID = new Random().nextInt(Integer.valueOf(((Long) movieRepository.count()).toString())) + 4;
			movie = movieRepository.findById(Long.valueOf(radomID.toString())).get();
			if (game.getMoviesAnswered().contains(movie))
				movie = null;
		} while (movie == null);
		game.getMoviesAnswered().add(movie);
		question.getMovies().add(movie);
	}

	/**
	 * Finds the user logged in
	 * 
	 * @return {@link User} User logged in
	 */
	private User toFindCurrentUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userRepository.findByUsername(((UserDetails) principal).getUsername());
	}
}

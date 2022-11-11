package com.movies.battle.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Game")
public class Game {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	private long points;

	@Column
	private int errors;

	@Column
	private boolean currentGame;

	@ManyToOne
	@JoinTable(name = "game_user", joinColumns = @JoinColumn(name = "game_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private User user;

	@OneToOne
	@MapsId
	private Question currentQuestion = new Question();
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(joinColumns = @JoinColumn(name = "game_id"), inverseJoinColumns = @JoinColumn(name = "movie_id"))
	private Set<Movie> moviesAnswered;
	
	@Column
	private Long currentQuestionID;

	public Game() {
	}

	public Game(User user) {
		this.points = 0;
		this.currentGame = true;
		this.user = user;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPoints() {
		return points;
	}

	public void setPoints(long points) {
		this.points = points;
	}

	public int getErrors() {
		return errors;
	}

	public void setErrors(int errors) {
		this.errors = errors;
	}

	public boolean isCurrentGame() {
		return currentGame;
	}

	public void setCurrentGame(boolean currentGame) {
		this.currentGame = currentGame;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getCurrentQuestionID() {
		return currentQuestionID;
	}

	public void setCurrentQuestionID(Long currentQuestionID) {
		this.currentQuestionID = currentQuestionID;
	}

	public Set<Movie> getMoviesAnswered() {
		return moviesAnswered;
	}

	public void setMoviesAnswered(Set<Movie> moviesAnswered) {
		this.moviesAnswered = moviesAnswered;
	}
}

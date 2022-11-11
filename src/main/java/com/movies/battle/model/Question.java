package com.movies.battle.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class Question {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(joinColumns = @JoinColumn(name = "question_id"), inverseJoinColumns = @JoinColumn(name = "movie_id"))
	private List<Movie> movies = new ArrayList<Movie>();
	
	private static final String QUESTION = "Which movie has the highest score? (ratings x votes)";

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Movie> getMovies() {
		return movies;
	}

	public void setMovies(List<Movie> movies) {
		this.movies = movies;
	}

	public static String getQuestion() {
		return QUESTION;
	}
}

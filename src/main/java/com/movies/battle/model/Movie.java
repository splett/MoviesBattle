package com.movies.battle.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Movie {

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@JsonIgnore
	@Column
	private String imdbID;
	
	@Column
	private String Title;
	
	@JsonIgnore
	@Column
	private Float imdbRating;
	
	@JsonIgnore
	@Column
	private Long imdbVotes;
	
	public Movie() {
	}

	public Movie(String imbdID, String Title, Float imdbRating, Long imdbVotes) {
		this.imdbID = imbdID;
		this.Title = Title;
		this.imdbRating = imdbRating;
		this.imdbVotes = imdbVotes;
	}

	public String getImdbID() {
		return imdbID;
	}

	public void setImdbID(String imdbID) {
		this.imdbID = imdbID;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public Float getImdbRating() {
		return imdbRating;
	}

	public void setImdbRating(Float imdbRating) {
		this.imdbRating = imdbRating;
	}

	public Long getImdbVotes() {
		return imdbVotes;
	}

	public void setImdbVotes(Long imdbVotes) {
		this.imdbVotes = imdbVotes;
	}
	
	@JsonIgnore
	public float getScore() {
		return this.imdbVotes * this.imdbRating;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}

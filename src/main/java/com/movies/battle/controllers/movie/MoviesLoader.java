package com.movies.battle.controllers.movie;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Random;

import org.apache.tomcat.util.json.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.client.RestTemplate;

import com.movies.battle.model.Movie;

public class MoviesLoader {

	private static String OMDB_API_KEY = "9a3a28a4";
	private static String OMDB_API_URL = "http://www.omdbapi.com/";
	private static String IMDB_TOP_MOVIES_URL = "http://www.imdb.com/chart/top";

	/**
	 * Generates a random number and, based on that number, goes to
	 * http://www.imdb.com/chart/top and captures the corresponding IMDBId. Used to
	 * capture the IMDBId of movies that are known to the public.
	 * 
	 * @return {@link String} imdbID
	 * @throws IOException if there is an error related to the webscraping performed by Jsoup
	 */
	public String extractRandomIMDBIDFromTop250Movies() throws IOException {
		final Document document = Jsoup.connect(IMDB_TOP_MOVIES_URL).get();
		int randomNumber = new Random().nextInt(250);
		int count = 0;
		for (Element row : document.select("table.chart.full-width tr")) {
			count++;
			if (count == randomNumber) {
				final String imdbIDString = row.select(".titleColumn").html();
				return imdbIDString.split("/")[2];
			}
		}
		return null;
	}

	/**
	 * Access OMDBApi and return Movie object populated based on ImdbID of random
	 * movies found in IMDB Top 250
	 * 
	 * @return {@link Movie} Movie
	 */
	@SuppressWarnings("unchecked")
	public Movie findMovieOnOMDBApi() {
		Movie movie = null;
		try {
			String uri = OMDB_API_URL + "?apikey=" + OMDB_API_KEY + "&type=movie&i=" + extractRandomIMDBIDFromTop250Movies();
			RestTemplate restTemplate = new RestTemplate();
			LinkedHashMap<String, String> movieResult = null;
			movieResult = (LinkedHashMap<String, String>) new JSONParser(restTemplate.getForObject(uri, String.class))
					.parse();
			movie = new Movie(movieResult.get("imdbID"), movieResult.get("Title"),
					Float.valueOf(movieResult.get("imdbRating")),
					Long.valueOf(movieResult.get("imdbVotes").replace(",", "")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return movie;
	}

	/**
	 * Checks if movie is valid or not based on rating and votes informations.
	 * 
	 * @param movie {@link Movie}
	 * @return {@link Boolean} true if movie is valid, otherwise false.
	 */
	public boolean movieIsValid(Movie movie) {
		if (movie == null)
			return false;
		if (movie.getImdbRating() == null)
			return false;
		if (movie.getImdbVotes() == null)
			return false;
		return true;
	}
}

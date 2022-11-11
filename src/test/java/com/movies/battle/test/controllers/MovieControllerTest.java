package com.movies.battle.test.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.movies.battle.repository.MovieRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MovieRepository movieRepository;

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	void whenAdminUserTryToAddNewMovies_ShouldAddNewMovies() throws Exception {
		long countMoviesInicial = movieRepository.count();
		long moviesToAdd = 2;

		mockMvc.perform(get("/admin/addNewMovies/" + moviesToAdd, 42L).contentType("application/json"))
				.andExpect(status().isOk()).andReturn();
		long countMoviesFinal = movieRepository.count();

		assertThat(countMoviesFinal - countMoviesInicial).isEqualTo(moviesToAdd);
	}
}

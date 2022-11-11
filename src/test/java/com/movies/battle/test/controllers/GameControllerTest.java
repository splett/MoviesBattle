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
import org.springframework.test.web.servlet.MvcResult;

import com.movies.battle.model.Game;
import com.movies.battle.repository.GameRepository;
import com.movies.battle.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
	void whenStartGame_thenSavesNewGame() throws Exception {
		MvcResult result = mockMvc.perform(get("/startGame", 42L)).andExpect(status().isOk()).andReturn();
		Game game = gameRepository.findByUserAndCurrentGame(userRepository.findByUsername("admin"), true);
		assertThat(game).isNotNull();
		assertThat(result.getResponse().getContentAsString()).isEqualToIgnoringWhitespace("Game Started");
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
	void whenTryToStartAnotherGame_shouldReturnsMessage() throws Exception {
		// Starts the first game
		mockMvc.perform(get("/startGame", 42L)).andExpect(status().isOk()).andReturn();

		// Trying to start another game
		MvcResult result = mockMvc.perform(get("/startGame", 42L)).andExpect(status().isOk()).andReturn();

		assertThat(result.getResponse().getContentAsString()).isEqualToIgnoringWhitespace("There are a current game running. Finish it or end.");
	}
	
	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
	void whenEndGameValid_thenEndTheGame() throws Exception {
		mockMvc.perform(get("/startGame", 42L)).andExpect(status().isOk()).andReturn();

		MvcResult result = mockMvc.perform(get("/endGame", 42L)).andExpect(status().isOk()).andReturn();

		Game game = gameRepository.findByUserAndCurrentGame(userRepository.findByUsername("admin"), true);
		assertThat(game).isNull();
		assertThat(result.getResponse().getContentAsString()).isEqualToIgnoringWhitespace("Game finished. Total points: 0");
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
	void whenTryToEndGameWithoutStart_shouldReturnsMessage() throws Exception {
		MvcResult result = mockMvc.perform(get("/endGame", 42L)).andExpect(status().isOk()).andReturn();
		assertThat(result.getResponse().getContentAsString()).isEqualToIgnoringWhitespace("There are no games running.");
	}
	
}

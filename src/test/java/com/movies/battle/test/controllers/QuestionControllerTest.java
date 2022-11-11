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
public class QuestionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
	void whenTryToGetQuestionWithoutStartGame__shouldReturnsMessage() throws Exception {
		//Ending any current game
		mockMvc.perform(get("/endGame", 42L)).andExpect(status().isOk()).andReturn();
		MvcResult result = mockMvc.perform(get("/question", 42L)).andExpect(status().isOk()).andReturn();
		Game game = gameRepository.findByUserAndCurrentGame(userRepository.findByUsername("admin"), true);
		assertThat(game).isNull();
		assertThat(result.getResponse().getContentAsString()).isEqualToIgnoringWhitespace("There are no games running. Try to start one!");
	}
	
	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
	void whenTryToAnswerWithoutStartGame__shouldReturnsMessage() throws Exception {
		//Ending any current game
		mockMvc.perform(get("/endGame", 42L)).andExpect(status().isOk()).andReturn();
		
		MvcResult result = mockMvc.perform(get("/answer/1", 42L)).andExpect(status().isOk()).andReturn();
		Game game = gameRepository.findByUserAndCurrentGame(userRepository.findByUsername("admin"), true);
		assertThat(game).isNull();
		assertThat(result.getResponse().getContentAsString()).isEqualToIgnoringWhitespace("There are no games running. Try to start one!");
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
	void whenTryToGetQuestionWithStartedGame__shouldReturnsQuestion() throws Exception {
		
		//Starting a new game
		mockMvc.perform(get("/startGame", 42L)).andExpect(status().isOk()).andReturn();
		
		MvcResult result = mockMvc.perform(get("/question", 42L)).andExpect(status().isOk()).andReturn();
		Game game = gameRepository.findByUserAndCurrentGame(userRepository.findByUsername("admin"), true);
		assertThat(game).isNotNull();
		assertThat(game.getCurrentQuestionID()).isNotNull();
		assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotEqualToIgnoringWhitespace("There are no games running. Try to start one!");
	}
	
	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
	void whenTryToAnswerWithStartedGame__shouldReturnsResult() throws Exception {
		//Starting a new game
		mockMvc.perform(get("/startGame", 42L)).andExpect(status().isOk()).andReturn();
		//Verify the question 
		mockMvc.perform(get("/question", 42L)).andExpect(status().isOk()).andReturn();
		Long firstQuestion = gameRepository.findByUserAndCurrentGame(userRepository.findByUsername("admin"), true).getCurrentQuestionID();
		
		MvcResult result = mockMvc.perform(get("/answer/1", 42L)).andExpect(status().isOk()).andReturn();
		Game game = gameRepository.findByUserAndCurrentGame(userRepository.findByUsername("admin"), true);
		Long lastQuestion = game.getCurrentQuestionID();

		assertThat(lastQuestion).isNotNull();
		assertThat(firstQuestion).isNotEqualTo(lastQuestion);
		assertThat(result.getResponse().getContentAsString()).containsIgnoringCase("Answer is");
	}
	
	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
	void whenTryToAnswerWithInvalidOption__shouldReturnsResult() throws Exception {
		//Starting a new game
		mockMvc.perform(get("/startGame", 42L)).andExpect(status().isOk()).andReturn();
		//Verify the question 
		mockMvc.perform(get("/question", 42L)).andExpect(status().isOk()).andReturn();
		
		MvcResult result = mockMvc.perform(get("/answer/4", 42L)).andExpect(status().isOk()).andReturn();
		
		assertThat(result.getResponse().getContentAsString()).containsIgnoringCase("Invalid Option. Choose 1 or 2.");
	}
}

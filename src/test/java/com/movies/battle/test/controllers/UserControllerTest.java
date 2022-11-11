package com.movies.battle.test.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movies.battle.model.User;
import com.movies.battle.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserRepository userRepository;

	@Test
	void withValidInput_thenSavesNewUser() throws Exception {
		User user = new User("name", "test@test.com", "test", "test");
		MvcResult result = mockMvc.perform(post("/signup", 42L).contentType("application/json")
				.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk()).andReturn();

		User userEntity = userRepository.findByUsername("test");
		assertThat(userEntity.getEmail()).isEqualTo("test@test.com");
		assertThat(userEntity.getName()).isEqualTo("name");
		assertThat(userEntity.getUsername()).isEqualTo("test");
		assertThat(result.getResponse().getContentAsString()).isEqualToIgnoringWhitespace("User has been registered successfully");
	}
	
	@Test
	void withValidInputButWithAnExistingUser_shouldReturnsMessage() throws Exception {
		User user = new User("name", "test@test.com", "test", "test");
		
		// Creating a new user
		mockMvc.perform(post("/signup", 42L).contentType("application/json")
				.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk()).andReturn();

		// Trying to create again
		MvcResult result = mockMvc.perform(post("/signup", 42L).contentType("application/json")
				.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk()).andReturn();
		
		assertThat(result.getResponse().getContentAsString()).isEqualToIgnoringWhitespace("There is already a user registered with the username provided.");
	}
	
	@Test
	void withInvalid_shouldReturnsMessage() throws Exception {
		User user = new User(null, null, null, "test");

		mockMvc.perform(post("/signup", 42L).contentType("application/json")
				.content(objectMapper.writeValueAsString(user)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Name is mandatory"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.email").value("E-mail is mandatory"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Username is mandatory"))
				.andReturn();
		
	}
}

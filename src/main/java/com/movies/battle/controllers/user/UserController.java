package com.movies.battle.controllers.user;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.movies.battle.model.User;
import com.movies.battle.repository.RoleRepository;
import com.movies.battle.repository.UserRepository;

@Controller
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;

	/**
	 * Used to create new users/players
	 * 
	 * @param user {@link User} to be created
	 * @return {@link ResponseEntity} with messages informing if the user was
	 *         created correctly or not.
	 */
	@PostMapping("/signup")
	public ResponseEntity<Object> createUser(@RequestBody @Valid User user) {
		User userExists = userRepository.findByUsername(user.getUsername());
		user.setRoles(Arrays.asList(roleRepository.findByRole("USER")));
		if (userExists != null) {
			return ResponseEntity.ok("There is already a user registered with the username provided.");
		} else {
			userRepository.save(user);
			return ResponseEntity.ok("User has been registered successfully");
		}
	}

	/**
	 * Used to inform user data validation messages
	 * 
	 * @param ex {@link MethodArgumentNotValidException} used for @Valid
	 * @return {@link ResponseEntity} with validation messages
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return ResponseEntity.badRequest().body(errors);
	}
}

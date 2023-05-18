package pt.ua.deti.tqs.shopbackend.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ua.deti.tqs.shopbackend.model.auth.LoginRequest;
import pt.ua.deti.tqs.shopbackend.model.auth.LoginResponse;
import pt.ua.deti.tqs.shopbackend.model.auth.RegisterRequest;
import pt.ua.deti.tqs.shopbackend.model.dto.ErrorDTO;
import pt.ua.deti.tqs.shopbackend.services.AuthService;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthControllers {
	private final AuthService authService;
	private static final ErrorDTO INVALID_CREDENTIAlS = new ErrorDTO("Invalid credentials");
	private static final ErrorDTO USER_ALREADY_EXISTS = new ErrorDTO("User already exists");

	public AuthControllers(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/signup")
	public ResponseEntity<Object> signup(@RequestBody RegisterRequest registerRequest) {
		log.info("Signup request");
		String response = authService.signup(registerRequest);
		if (response == null) {
			log.error("User already exists");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.contentType(APPLICATION_JSON)
					.body(USER_ALREADY_EXISTS);
		}
		log.info("User created");
		String token = authService.login(new LoginRequest(registerRequest.getEmail(), registerRequest.getPassword())).getToken();
		log.info("Token generated");
		return ResponseEntity.status(HttpStatus.CREATED)
					.contentType(APPLICATION_JSON)
					.body(new LoginResponse(token));
	}

	@GetMapping("/login")
	public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
		log.info("Login request");
		LoginResponse response = authService.login(loginRequest);
		if (response == null) {
			log.error("Invalid credentials");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.contentType(APPLICATION_JSON)
					.body(INVALID_CREDENTIAlS);
		}
		log.info("Login successful");
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(APPLICATION_JSON)
				.body(response);
	}

}

package pt.ua.deti.tqs.shopbackend.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ua.deti.tqs.shopbackend.model.auth.LoginRequest;
import pt.ua.deti.tqs.shopbackend.model.auth.LoginResponse;
import pt.ua.deti.tqs.shopbackend.model.auth.RegisterRequest;
import pt.ua.deti.tqs.shopbackend.model.dto.ClientDTO;
import pt.ua.deti.tqs.shopbackend.model.dto.ErrorDTO;
import pt.ua.deti.tqs.shopbackend.model.dto.SuccessDTO;
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
		log.info("AuthControllers -- Signup -- request received");
		String response = authService.signup(registerRequest);
		if (response == null) {
			log.error("AuthControllers -- Signup -- " + USER_ALREADY_EXISTS.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.contentType(APPLICATION_JSON)
					.body(USER_ALREADY_EXISTS);
		}
		log.info("AuthControllers -- Signup -- " + response);
		String token = authService.login(new LoginRequest(registerRequest.getEmail(), registerRequest.getPassword())).getToken();
		log.info("AuthControllers -- Signup -- Login successful");
		return ResponseEntity.status(HttpStatus.CREATED)
				.contentType(APPLICATION_JSON)
				.body(new LoginResponse(token));
	}

	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
		log.info("AuthControllers -- Login -- request received");
		LoginResponse response = authService.login(loginRequest);
		if (response == null) {
			log.error("AuthControllers -- Login -- " + INVALID_CREDENTIAlS.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.contentType(APPLICATION_JSON)
					.body(INVALID_CREDENTIAlS);
		}
		log.info("AuthControllers -- Login -- " + response);
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(APPLICATION_JSON)
				.body(response);
	}

	@PutMapping("/logout")
	@PreAuthorize("@authService.isAuthenticated(#token)")
	public ResponseEntity<Object> logout(@RequestHeader("Authorization") String token){
		log.info("AuthControllers -- Logout -- request received");
		if (Boolean.TRUE.equals(authService.logout(token))){
			log.info("AuthControllers -- Logout -- Logout successful");
			return ResponseEntity.status(HttpStatus.OK)
					.contentType(APPLICATION_JSON)
					.body(new SuccessDTO("Logout successful"));
		}
		log.error("AuthControllers -- Logout -- " + INVALID_CREDENTIAlS.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.contentType(APPLICATION_JSON)
				.body(INVALID_CREDENTIAlS);
	}

	@GetMapping("/me")
	@PreAuthorize("@authService.isAuthenticated(#token)")
	public ResponseEntity<Object> me(@RequestHeader("Authorization") String token){
		log.info("AuthControllers -- Me -- request received");
		ClientDTO client = authService.currentUser(token);
		if (client == null) {
			log.error("AuthControllers -- Me -- Client not found");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.contentType(APPLICATION_JSON)
					.body(new ErrorDTO("Client not found"));
		}
		log.info("AuthControllers -- Me -- Client found");
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(APPLICATION_JSON)
				.body(client);
	}

}

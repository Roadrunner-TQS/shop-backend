package pt.ua.deti.tqs.shopbackend.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ua.deti.tqs.shopbackend.model.auth.LoginRequest;
import pt.ua.deti.tqs.shopbackend.model.auth.RegisterRequest;
import pt.ua.deti.tqs.shopbackend.services.AuthService;


@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthControllers {
	private final AuthService authService;

	public AuthControllers(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/signup")
	public ResponseEntity<Object> signup(@RequestBody RegisterRequest registerRequest) {
		return null;
	}

	@GetMapping("/login")
	public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
		return null;
	}


}

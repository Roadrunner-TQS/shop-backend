package pt.ua.deti.tqs.shopbackend.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pt.ua.deti.tqs.shopbackend.config.JwtConfig;

import java.security.Key;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class JwtTokenServiceTest {
	
	@Mock
	private JwtConfig jwtConfig;

	private Key jwtKey;
	private JwtTokenService jwtTokenService;

	@BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        jwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        jwtTokenService = new JwtTokenService(jwtConfig, jwtKey);
    }

	@Test
	@DisplayName("When generateToken is called then a valid token is returned")
	public void generateToken_ShouldReturnValidToken() {
		when(jwtConfig.getExpiration()).thenReturn(600);
		String token = jwtTokenService.generateToken("username");

		assertNotNull(token);
		assertTrue(jwtTokenService.validateToken(token));
	}

	@Test
	@DisplayName("When an invalid token is passed to validateToken then false is returned")
	public void validateToken_withInvalidToken_ShouldReturnFalse() {
		assertFalse(jwtTokenService.validateToken("invalid_token"));
	}

	@Test
	@DisplayName("When an expired token is passed to validateToken then false is returned")
	public void validateToken_withExpiredToken_ShouldReturnFalse() throws InterruptedException {
		when(jwtConfig.getExpiration()).thenReturn(1);

		String token = jwtTokenService.generateToken("username");

		assertNotNull(token);
		Thread.sleep(1000);
		assertFalse(jwtTokenService.validateToken(token));
	}

	// Dúvida
	@Test
	@DisplayName("When a valid token is passed to getEmailFromToken then the email is returned")
	public void getEmailFromToken_ShouldReturnEmail() {
		String token = Jwts.builder().setSubject("username").signWith(jwtKey).compact();

		String email = jwtTokenService.getEmailFromToken(token);
		assertEquals("username", email);
	}
}

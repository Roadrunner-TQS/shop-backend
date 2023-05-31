package pt.ua.deti.tqs.shopbackend.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pt.ua.deti.tqs.shopbackend.config.JwtConfig;

import java.security.Key;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

class JwtTokenServiceTest {

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
	void generateToken_ShouldReturnValidToken() {
		when(jwtConfig.getExpiration()).thenReturn(600);
		String token = jwtTokenService.generateToken("username");

		assertNotNull(token);
		Assertions.assertTrue(jwtTokenService.validateToken(token));
	}

	@Test
	@DisplayName("When an invalid token is passed to validateToken then false is returned")
	void validateToken_withInvalidToken_ShouldReturnFalse() {
		Assertions.assertFalse(jwtTokenService.validateToken("invalid_token"));
	}

	@Test
	@DisplayName("When an expired token is passed to validateToken then false is returned")
	void validateToken_withExpiredToken_ShouldReturnFalse() throws InterruptedException {
		when(jwtConfig.getExpiration()).thenReturn(1);

		String token = jwtTokenService.generateToken("username");

		assertNotNull(token);
		await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
			Assertions.assertFalse(jwtTokenService.validateToken(token));
		});
		Assertions.assertFalse(jwtTokenService.validateToken(token));
	}

	@Test
	@DisplayName("When a valid token is passed to getEmailFromToken then the email is returned")
	void getEmailFromToken_ShouldReturnEmail() {
		String token = Jwts.builder().setSubject("username").signWith(jwtKey).compact();

		String email = jwtTokenService.getEmailFromToken(token);
		assertEquals("username", email);
	}
}

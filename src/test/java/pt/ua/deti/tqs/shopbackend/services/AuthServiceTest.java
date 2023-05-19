package pt.ua.deti.tqs.shopbackend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pt.ua.deti.tqs.shopbackend.data.ClientRepository;
import pt.ua.deti.tqs.shopbackend.model.Client;
import pt.ua.deti.tqs.shopbackend.model.auth.LoginRequest;
import pt.ua.deti.tqs.shopbackend.model.auth.LoginResponse;
import pt.ua.deti.tqs.shopbackend.model.auth.RegisterRequest;
import pt.ua.deti.tqs.shopbackend.model.dto.ClientDTO;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock(lenient = true)
	private ClientRepository clientRepository;

	@Mock
	private JwtTokenService jwtTokenService;

	@InjectMocks
	private AuthService authService;

	@Test
	void login_WithValidCredentials_ShouldReturnLoginResponse() {
		String email = "test@mail.com";
		String password = "password";

		LoginRequest loginRequest = new LoginRequest(email, password);
		Client client = new Client();
		client.setEmail(email);
		client.setPassword(password);
		when(clientRepository.findByEmail(email)).thenReturn(Optional.of(client));
		when(jwtTokenService.generateToken(email)).thenReturn("token");
		when(jwtTokenService.getEmailFromToken("token")).thenReturn(email);
		when(jwtTokenService.validateToken("token")).thenReturn(true);

		LoginResponse loginResponse = authService.login(loginRequest);

		assertNotNull(loginResponse);
		assertEquals("token", loginResponse.getToken());
		assertTrue(authService.isAuthenticated("token"));
	}

	@Test
	void login_WithInvalidCredentials_ShouldReturnNull() {
		String email = "test@mail.com";
		String password = "password";

		LoginRequest loginRequest = new LoginRequest(email, password);

		when(clientRepository.findByEmail(email)).thenReturn(Optional.empty());

		LoginResponse loginResponse = authService.login(loginRequest);

		assertNull(loginResponse);
		assertFalse(authService.isAuthenticated("token"));
	}
	
	@Test
	void signup_WithNewUser_ShouldCreateUserAndReturnSuccessMessage() {
		RegisterRequest registerRequest = new RegisterRequest("test@mail.com", "password", "firstName", "lastName",
				"phone", null);
		when(clientRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());

		String response = authService.signup(registerRequest);

		assertEquals("User Created", response);
		verify(clientRepository, times(1)).save(any(Client.class));
	}
	
	@Test
	void signup_WithExistingUser_ShouldReturnErrorMessage() {
		RegisterRequest registerRequest = new RegisterRequest("test@mail.com", "password", "firstName", "lastName",
				"phone", "ROLE_USER");
		when(clientRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(new Client()));

		String response = authService.signup(registerRequest);

		assertNull(response);
		verify(clientRepository, times(0)).save(any(Client.class));
	}

	@Test
	void isAuthenticated_WithValidToken_ShouldReturnTrue() {
		String email = "test@mail.com";
		String password = "password";
		RegisterRequest registerRequest = new RegisterRequest(email, password, "firstName", "lastName",
				"phone", "ROLE_USER");

		authService.signup(registerRequest);
		Client client = new Client();
		client.setEmail(email);
		client.setPassword(password);
		when(clientRepository.findByEmail(email)).thenReturn(Optional.of(client));
		when(jwtTokenService.generateToken(email)).thenReturn("token");

		String token = authService.login(new LoginRequest(email, password)).getToken();

		when(jwtTokenService.getEmailFromToken(token)).thenReturn(email);
		when(jwtTokenService.validateToken(token)).thenReturn(true);

		assertTrue(authService.isAuthenticated(token));
		verify(clientRepository, times(1)).save(any(Client.class));
	}

	@Test
	void isAuthenticated_WithInvalidToken_ShouldReturnFalse() {
		String email = "test@mail.com";
		String password = "password";
		RegisterRequest registerRequest = new RegisterRequest(email, password, "firstName", "lastName",
				"phone", "ROLE_USER");

		authService.signup(registerRequest);
		Client client = new Client();
		client.setEmail(email);
		client.setPassword(password);
		when(clientRepository.findByEmail(email)).thenReturn(Optional.of(client));
		when(jwtTokenService.generateToken(email)).thenReturn("token");

		String token = authService.login(new LoginRequest(email, password)).getToken();

		when(jwtTokenService.getEmailFromToken(token)).thenReturn(email);
		String invalid_token = "invalid";
		when(jwtTokenService.validateToken(invalid_token)).thenReturn(false);

		assertFalse(authService.isAuthenticated(invalid_token));
		verify(clientRepository, times(1)).save(any(Client.class));
	}

	@Test
	void logout_WithValidToken_ShouldReturnTrue() {
		String email = "test@gmail.com";
		String password = "password";
		RegisterRequest registerRequest = new RegisterRequest(email, password, "firstName", "lastName",
				"phone", "ROLE_USER");

		authService.signup(registerRequest);
		Client client = new Client();
		client.setEmail(email);
		client.setPassword(password);
		when(clientRepository.findByEmail(email)).thenReturn(Optional.of(client));
		when(jwtTokenService.generateToken(email)).thenReturn("token");

		String token = authService.login(new LoginRequest(email, password)).getToken();

		when(jwtTokenService.getEmailFromToken(token)).thenReturn(email);

		assertTrue(authService.logout(token));
		verify(clientRepository, times(1)).save(any(Client.class));
	}

	@Test
	void logout_WithInvalidToken_ShouldReturnFalse() {
		String email = "test@mail.com";
		String password = "password";
		RegisterRequest registerRequest = new RegisterRequest(email, password, "firstName", "lastName",
				"phone", "ROLE_USER");

		authService.signup(registerRequest);
		Client client = new Client();
		client.setEmail(email);
		client.setPassword(password);
		when(clientRepository.findByEmail(email)).thenReturn(Optional.of(client));

		String invalid_token = "invalid";
		when(jwtTokenService.getEmailFromToken(invalid_token)).thenReturn(null);

		assertFalse(authService.logout(invalid_token));
		verify(clientRepository, times(1)).save(any(Client.class));
	}

	@Test
	void getCurrentUser_WithValidToken_ShouldReturnCurrentUser() {
		String token = "valid_token";
		String email = "test@example.com";
		Client client = new Client();
		client.setEmail(email);
		ClientDTO expectedClientDTO = new ClientDTO();
		expectedClientDTO.setEmail(email);
		when(jwtTokenService.getEmailFromToken(token)).thenReturn(email);
		when(clientRepository.findByEmail(email)).thenReturn(Optional.of(client));
		ClientDTO result = authService.currentUser(token);
		assertNotNull(result);
		assertEquals(expectedClientDTO, result);
		verify(jwtTokenService, times(1)).getEmailFromToken(token);
		verify(clientRepository, times(1)).findByEmail(email);
	}

	@Test
	 void testCurrentUser_InvalidToken_ReturnsNull() {
		String token = "invalid_token";
		when(jwtTokenService.getEmailFromToken(token)).thenReturn(null);
		ClientDTO result = authService.currentUser(token);
		assertNull(result);
		verify(jwtTokenService, times(1)).getEmailFromToken(token);
		verify(clientRepository, never()).findByEmail(anyString());
	}
}

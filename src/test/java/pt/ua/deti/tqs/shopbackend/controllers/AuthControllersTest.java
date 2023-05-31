package pt.ua.deti.tqs.shopbackend.controllers;


import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pt.ua.deti.tqs.shopbackend.model.auth.LoginRequest;
import pt.ua.deti.tqs.shopbackend.model.auth.LoginResponse;
import pt.ua.deti.tqs.shopbackend.model.auth.RegisterRequest;
import pt.ua.deti.tqs.shopbackend.model.dto.ClientDTO;
import pt.ua.deti.tqs.shopbackend.services.AuthService;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@WebMvcTest(AuthControllers.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    private RegisterRequest registerRequest;

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("client@gmail.com");
        registerRequest.setPassword("123456");
        registerRequest.setPhone("123456789");
        registerRequest.setFirstName("Client");
        registerRequest.setLastName("Client");

        loginRequest = new LoginRequest("client@gmail.com", "123456");
    }

    @Test
    @DisplayName("When signup with user that does not exist, then returns created and token")
    void whenSignUp_UserDoesNotExist_thenReturnsCreatedAndToken() {
        LoginResponse response = new LoginResponse("Token");
        when(authService.signup(any())).thenReturn("Token");
        when(authService.login(any())).thenReturn(response);

        RestAssuredMockMvc.given()
                .contentType("application/json")
                .body(registerRequest)
                .when()
                .post("/api/auth/signup")
                .then()
                .statusCode(201)
                .body("token", equalTo(response.getToken()));

        verify(authService, times(1)).signup(registerRequest);
        verify(authService, times(1)).login(loginRequest);

    }

    @Test
    @DisplayName("When signup with user that already exists, then returns bad request")
    void whenSignUp_UserAlreadyExists_thenReturnsBadRequest() {
        when(authService.signup(any())).thenReturn(null);

        RestAssuredMockMvc.given()
                .contentType("application/json")
                .body(registerRequest)
                .when()
                .post("/api/auth/signup")
                .then()
                .statusCode(400)
                .body("message", equalTo("User already exists"));

        verify(authService, times(1)).signup(registerRequest);
        verify(authService, times(0)).login(loginRequest);
    }

    @Test
    @DisplayName("When login with user that exists, then returns token")
    void whenLogin_UserExists_thenReturnsToken() {
        LoginResponse response = new LoginResponse("Token");
        when(authService.login(any())).thenReturn(response);

        RestAssuredMockMvc.given()
                .contentType("application/json")
                .body(loginRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("token", equalTo(response.getToken()));

        verify(authService, times(0)).signup(registerRequest);
        verify(authService, times(1)).login(loginRequest);
    }

    @Test
    @DisplayName("When login with invalid data, then returns bad request")
    void whenLogin_InvalidData_thenReturnsBadRequest() {
        when(authService.login(any())).thenReturn(null);

        RestAssuredMockMvc.given()
                .contentType("application/json")
                .body(loginRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Invalid credentials"));

        verify(authService, times(0)).signup(registerRequest);
        verify(authService, times(1)).login(loginRequest);
    }

    @Test
    @WithMockUser
    @DisplayName("When logout with valid token, then returns ok")
    void WhenLogout_ValidToken_ThenReturnsOK() {
        String validToken = "valid_token";

        when(authService.logout(validToken)).thenReturn(true);

        RestAssuredMockMvc.given()
                .header("Authorization", validToken)
                .contentType("application/json")
                .when()
                .put("/api/auth/logout")
                .then()
                .statusCode(200)
                .body("success", equalTo("Logout successful"));

        verify(authService, times(1)).logout(validToken);
    }

    @Test
    @WithMockUser
    @DisplayName("When logout with invalid token, then returns unauthorized")
    void WhenLogout_InvalidToken_ThenReturnsUnauthorized() {
        String invalidToken = "invalid_token";

        when(authService.logout(invalidToken)).thenReturn(false);

        RestAssuredMockMvc.given()
                .header("Authorization", invalidToken)
                .contentType("application/json")
                .when()
                .put("/api/auth/logout")
                .then()
                .statusCode(400)
                .body("message", equalTo("Invalid credentials"));
    }

    @Test
    @WithMockUser
    @DisplayName("When me with valid token, then returns user")
    void WhenMe_ValidToken_ThenReturnsOk(){
        String validToken = "valid_token";
        ClientDTO client = new ClientDTO();
        client.setId(UUID.randomUUID());
        client.setEmail("client@gmail.com");
        client.setFirstName("Client");
        client.setLastName("Client");
        client.setPhone("123456789");

        when(authService.currentUser(validToken)).thenReturn(client);

        RestAssuredMockMvc.given()
                .header("Authorization", validToken)
                .contentType("application/json")
                .when()
                .get("/api/auth/me")
                .then()
                .statusCode(200)
                .body("email", equalTo(client.getEmail()))
                .body("firstName", equalTo(client.getFirstName()))
                .body("lastName", equalTo(client.getLastName()))
                .body("phone", equalTo(client.getPhone()));


        verify(authService, times(1)).currentUser(validToken);
    }

    @Test
    @WithMockUser
    @DisplayName("When me with invalid token, then returns unauthorized")
    void WhenMe_InvalidToken_ThenReturnsBadRequest(){
        String invalid = "invalid_token";

        when(authService.currentUser(invalid)).thenReturn(null);

        RestAssuredMockMvc.given()
                .header("Authorization", invalid)
                .contentType("application/json")
                .when()
                .get("/api/auth/me")
                .then()
                .statusCode(400)
                .body("message", equalTo("Client not found"));


        verify(authService, times(1)).currentUser(invalid);
    }

}
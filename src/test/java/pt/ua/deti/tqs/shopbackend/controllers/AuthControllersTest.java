package pt.ua.deti.tqs.shopbackend.controllers;


import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pt.ua.deti.tqs.shopbackend.model.auth.LoginRequest;
import pt.ua.deti.tqs.shopbackend.model.auth.LoginResponse;
import pt.ua.deti.tqs.shopbackend.model.auth.RegisterRequest;
import pt.ua.deti.tqs.shopbackend.services.AuthService;

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

}
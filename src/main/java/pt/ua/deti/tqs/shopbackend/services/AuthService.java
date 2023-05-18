package pt.ua.deti.tqs.shopbackend.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.shopbackend.data.ClientRepository;
import pt.ua.deti.tqs.shopbackend.model.auth.LoginRequest;
import pt.ua.deti.tqs.shopbackend.model.auth.LoginResponse;
import pt.ua.deti.tqs.shopbackend.model.auth.RegisterRequest;


import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AuthService {
    private final ClientRepository clientRepository;
    private final JwtTokenService jwtTokenService;

    private static Map<String, String> whiteList = new HashMap<>();

    public AuthService(ClientRepository clientRepository, JwtTokenService jwtTokenService) {
        this.clientRepository = clientRepository;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        return null;
    }

    public String signup(RegisterRequest cliente) {
        return null;
    }

    public Boolean logout(String token) {
        return false;
    }

    public Boolean isAuthenticated(String token) {
        return false;
    }
}

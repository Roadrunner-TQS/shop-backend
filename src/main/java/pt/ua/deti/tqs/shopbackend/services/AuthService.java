package pt.ua.deti.tqs.shopbackend.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.shopbackend.data.ClientRepository;
import pt.ua.deti.tqs.shopbackend.model.Client;
import pt.ua.deti.tqs.shopbackend.model.auth.LoginRequest;
import pt.ua.deti.tqs.shopbackend.model.auth.LoginResponse;
import pt.ua.deti.tqs.shopbackend.model.auth.RegisterRequest;
import pt.ua.deti.tqs.shopbackend.model.dto.ClientDTO;
import pt.ua.deti.tqs.shopbackend.model.enums.Roles;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AuthService {
    private static final String BEARER = "Bearer ";
    private final ClientRepository clientRepository;
    private final JwtTokenService jwtTokenService;

    private static final Map<String, String> whiteList = new HashMap<>();

    public AuthService(ClientRepository clientRepository, JwtTokenService jwtTokenService) {
        this.clientRepository = clientRepository;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        log.info("AuthService -- Login -- request received");
        Client cliente = clientRepository.findByEmail(loginRequest.getEmail()).orElse(null);
        if (cliente != null && cliente.checkPassword(loginRequest.getPassword())) {
            String token = jwtTokenService.generateToken(loginRequest.getEmail());
            whiteList.put(cliente.getEmail(), token);
            log.info("AuthService -- Login -- Login successful");
            return new LoginResponse(token);
        }
        log.error("AuthService -- Login -- Invalid credentials");
        return null;
    }

    public String signup(RegisterRequest cliente) {
        log.info("AuthService -- Signup -- request received");
        if (clientRepository.findByEmail(cliente.getEmail()).isEmpty()) {
            ModelMapper modelMapper = new ModelMapper();
            Client client = modelMapper.map(cliente, Client.class);
            client.setPassword(cliente.getPassword());
            if (client.getRole() == null) {
                client.setRole(Roles.valueOf("ROLE_USER"));
            }
            clientRepository.save(client);
            log.info("AuthService -- Signup  -- Signup successful");
            return "User Created";
        }
        log.error("AuthService -- Signup -- User already exists");
        return null;
    }

    public Boolean logout(String tokenRequest) {
        log.info("AuthService -- Logout  -- request received");
        String token = tokenRequest.replace(BEARER, "");
        String email = jwtTokenService.getEmailFromToken(token);
        if (email != null && whiteList.containsKey(email) && whiteList.get(email).equals(token)) {
            log.info("AuthService -- Logout -- Logout successful");
            whiteList.remove(email);
            return true;
        }
        log.error("AuthService -- Logout  -- Invalid token");
        return false;
    }

    public Boolean isAuthenticated(String tokenRequest) {
        log.info("AuthService -- Is authenticated  -- request received");
        String token = tokenRequest.replace(BEARER, "");
        if (whiteList.isEmpty()) {
            log.info("AuthService -- Is authenticated -- No tokens in whitelist");
            return false;
        }
        String email = jwtTokenService.getEmailFromToken(token);
        if (!jwtTokenService.validateToken(token) || !whiteList.containsKey(email)) {
            log.info("AuthService -- Is authenticated -- Invalid token");
            return false;
        }
        log.info("AuthService -- Is authenticated -- Token is valid");
        return whiteList.get(email).equals(token);
    }

    public ClientDTO currentUser(String tokenRequest) {
        log.info("AuthService -- Get current user -- request received");
        String token = tokenRequest.replace(BEARER, "");
        String email = jwtTokenService.getEmailFromToken(token);
        Client client = clientRepository.findByEmail(email).orElse(null);
        if (client != null) {
            log.info("AuthService -- Get current user -- User found");
            ModelMapper modelMapper = new ModelMapper();
            return modelMapper.map(client, ClientDTO.class);
        }
        log.error("AuthService -- Get current user -- User not found");
        return null;
    }

    void clearWhiteList() {
        whiteList.clear();
    }
}

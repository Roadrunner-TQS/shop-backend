package pt.ua.deti.tqs.shopbackend.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.shopbackend.data.ClientRepository;
import pt.ua.deti.tqs.shopbackend.model.Client;
import pt.ua.deti.tqs.shopbackend.model.auth.LoginRequest;
import pt.ua.deti.tqs.shopbackend.model.auth.LoginResponse;
import pt.ua.deti.tqs.shopbackend.model.auth.RegisterRequest;
import pt.ua.deti.tqs.shopbackend.model.enums.Roles;

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
        Client cliente = clientRepository.findByEmail(loginRequest.getEmail()).orElse(null);
        if (cliente != null && cliente.checkPassword(loginRequest.getPassword())) {
            String token = jwtTokenService.generateToken(loginRequest.getEmail());
            whiteList.put(cliente.getEmail(), token);
            log.info("Login successful");
            return new LoginResponse(token);
        }
        log.error("Invalid credentials");
        return null;
    }

    public String signup(RegisterRequest cliente) {
        if (clientRepository.findByEmail(cliente.getEmail()).isEmpty()) {
            ModelMapper modelMapper = new ModelMapper();
            Client client = modelMapper.map(cliente, Client.class);
            client.setPassword(cliente.getPassword());
            if (client.getRole() == null) {
                client.setRole(Roles.valueOf("ROLE_USER"));
            }
            clientRepository.save(client);
            log.info("User created");
            return "User Created";
        }
        log.error("User already exists");
        return null;
    }

    public Boolean logout(String token) {
        log.info("AuthService -- Logout request");
        token = token.replace("Bearer ", "");
        String email = jwtTokenService.getEmailFromToken(token);
        if (email != null && whiteList.get(email).equals(token)) {
            whiteList.remove(email);
            return true;
        }
        return false;
    }

    public Boolean isAuthenticated(String token) {
        token = token.replace("Bearer ", "");
        if (whiteList.isEmpty()) {
            log.info("No users logged in");
            return false;
        }
        try {
            String email = jwtTokenService.getEmailFromToken(token);
            if (!jwtTokenService.validateToken(token) || !whiteList.containsKey(email)) {
                log.info("Invalid token");
                return false;
            }
            return whiteList.get(email).equals(token);

        } catch (Exception e) {
            log.info("Invalid token");
            return false;
        }
    }
}

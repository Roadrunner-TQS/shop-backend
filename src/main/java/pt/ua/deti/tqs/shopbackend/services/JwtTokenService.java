package pt.ua.deti.tqs.shopbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.shopbackend.config.JwtConfig;

import java.security.Key;

@Service
public class JwtTokenService {

    private final JwtConfig jwtConfig;
    private final Key jwtKey;

    @Autowired
    public JwtTokenService(JwtConfig jwtConfig, Key jwtKey) {
        this.jwtConfig = jwtConfig;
        this.jwtKey = jwtKey;
    }

    public String generateToken(String username) {
        return null;
    }

    public boolean validateToken(String token) {
        return false;
    }

    public String getEmailFromToken(String token) {
        return null;
    }
}

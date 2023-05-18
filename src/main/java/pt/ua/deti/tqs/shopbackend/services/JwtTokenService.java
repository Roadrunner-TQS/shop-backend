package pt.ua.deti.tqs.shopbackend.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.shopbackend.config.JwtConfig;

import java.security.Key;
import java.util.Date;

@Slf4j
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
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration() * 1000L);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(String token) {
        token = token.replace("Bearer ", "");
    
        try {
            Date expiration = Jwts.parserBuilder().setSigningKey(jwtKey).build().parseClaimsJws(token).getBody().getExpiration();
            return !expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    

    public String getEmailFromToken(String token) {
        token = token.replace("Bearer ", "");
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(jwtKey).build().parseClaimsJws(token);
            return claims.getBody().getSubject();
        } catch (Exception e) {
            return null;
        }
    }

}


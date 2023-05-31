package pt.ua.deti.tqs.shopbackend.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
}

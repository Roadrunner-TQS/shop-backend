package pt.ua.deti.tqs.shopbackend.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class LoginRequest {
    private String email;
    private String password;
}

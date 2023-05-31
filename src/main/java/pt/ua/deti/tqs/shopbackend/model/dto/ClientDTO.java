package pt.ua.deti.tqs.shopbackend.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ClientDTO {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;

}

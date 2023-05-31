package pt.ua.deti.tqs.shopbackend.services.pickup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackDto {
    private String firstName;
    private String lastName;
    private String email;
    private long phone;
    private UUID pickUpLocationId;
    private UUID shopId;
    private long date;
}

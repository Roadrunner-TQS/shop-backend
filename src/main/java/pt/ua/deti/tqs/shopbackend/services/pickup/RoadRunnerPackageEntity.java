package pt.ua.deti.tqs.shopbackend.services.pickup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RoadRunnerPackageEntity {
    @JsonProperty("message")
    private UUID id;
}

package pt.ua.deti.tqs.shopbackend.services.pickup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RoadRunnerGeneralEntity {
    @JsonProperty("message")
    private String msg;
}

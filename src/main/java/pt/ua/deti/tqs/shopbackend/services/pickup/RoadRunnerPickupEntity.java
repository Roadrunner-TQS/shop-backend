package pt.ua.deti.tqs.shopbackend.services.pickup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.modelmapper.ModelMapper;
import pt.ua.deti.tqs.shopbackend.model.PickUpLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoadRunnerPickupEntity {
    List<PickUpLocation> pickUpLocations = new ArrayList<>();

    @JsonProperty("message")
    private void unpackPickupPoints(List<Map<String, Object>> message) {
        for (Map<String, Object> el : message) {
            ModelMapper modelMapper = new ModelMapper();
            PickUpLocation pickUpLocation = modelMapper.map(el, PickUpLocation.class);
            pickUpLocation.setId(UUID.fromString(el.getOrDefault("id", null).toString()));
            pickUpLocations.add(pickUpLocation);
        }
    }
}

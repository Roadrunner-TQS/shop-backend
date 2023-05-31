package pt.ua.deti.tqs.shopbackend.services.pickup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.modelmapper.ModelMapper;
import pt.ua.deti.tqs.shopbackend.model.OrderStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RoadRunnerPackageHistoryEntity {
    private Map<UUID, List<OrderStatus>> statuses = new HashMap<>();

    @JsonProperty("statuses")
    public void unpackStatuses(Map<String, List<Map<String, Object>>> message) {
        message.forEach((String item, List<Map<String, Object>> value) -> {
            ModelMapper mapper = new ModelMapper();
            this.statuses.put(UUID.fromString(item), value.stream().map((Map<String, Object> v) -> {
                OrderStatus status = mapper.map(v, OrderStatus.class);
                status.setId(UUID.fromString(v.getOrDefault("id", null).toString()));
                return status;
            }).toList());
        });
    }
}

package pt.ua.deti.tqs.shopbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.ua.deti.tqs.shopbackend.model.OrderItem;
import pt.ua.deti.tqs.shopbackend.model.OrderStatus;
import pt.ua.deti.tqs.shopbackend.model.PickUpLocation;
import pt.ua.deti.tqs.shopbackend.model.PickUpService;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private UUID id;
    private List<OrderItem> items;
    private List<OrderStatus> orderStatus;
    private PickUpService pickUpService;
    private PickUpLocation pickUpLocation;
    private UUID trackingId;
    private String status;

}

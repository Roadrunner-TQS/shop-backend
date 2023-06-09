package pt.ua.deti.tqs.shopbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.sql.ast.tree.expression.Collation;
import pt.ua.deti.tqs.shopbackend.model.enums.Status;

import java.util.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(generator = "custom-uuid")
    @GenericGenerator(name = "custom-uuid", strategy = "pt.ua.deti.tqs.shopbackend.config.CustomUUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "client", nullable = false)
    private Client client;

    @Column(nullable = false)
    private long date;

    @ManyToOne
    @JoinColumn(name = "payment", nullable = false)
    private Payment payment;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<OrderStatus> orderStatus = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "pickup_id")
    private PickUpService pickUpService;

    @ManyToOne
    @JoinColumn(name = "pickup_location_id")
    private PickUpLocation pickUpLocation;

    @Column(nullable = true)
    private UUID trackingId;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private Status status;

    public void sort() {
        orderStatus.sort(Comparator.comparing(OrderStatus::getTimestamp));
    }


}
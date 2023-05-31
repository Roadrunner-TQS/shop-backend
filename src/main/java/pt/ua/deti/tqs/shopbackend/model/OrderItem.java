package pt.ua.deti.tqs.shopbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(generator = "custom-uuid")
    @GenericGenerator(name = "custom-uuid", strategy = "pt.ua.deti.tqs.shopbackend.config.CustomUUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "book", nullable = false)
    private Book book;

    @Column(nullable = false)
    private Integer quantity;

}


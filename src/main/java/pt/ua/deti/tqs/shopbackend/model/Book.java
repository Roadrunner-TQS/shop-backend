package pt.ua.deti.tqs.shopbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "books")
public class Book {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Column(nullable = false)
    private float price;

    @Column(nullable = true)
    private float discount = 0.00f;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer pages;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Category> categories = new HashSet<>();

}


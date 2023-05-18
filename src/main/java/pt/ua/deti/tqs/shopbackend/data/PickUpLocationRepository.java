package pt.ua.deti.tqs.shopbackend.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ua.deti.tqs.shopbackend.model.PickUpLocation;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PickUpLocationRepository extends JpaRepository<PickUpLocation, UUID> {
    Optional<PickUpLocation> findBySlug(String slug);
}

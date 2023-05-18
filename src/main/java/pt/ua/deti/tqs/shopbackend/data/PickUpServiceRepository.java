package pt.ua.deti.tqs.shopbackend.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ua.deti.tqs.shopbackend.model.PickUpService;

import java.util.UUID;

@Repository
public interface PickUpServiceRepository extends JpaRepository<PickUpService, UUID> {
}

package pt.ua.deti.tqs.shopbackend.services.pickup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.shopbackend.data.OrderRepository;
import pt.ua.deti.tqs.shopbackend.data.OrderStatusRepository;
import pt.ua.deti.tqs.shopbackend.data.PickUpLocationRepository;
import pt.ua.deti.tqs.shopbackend.data.PickUpServiceRepository;
import pt.ua.deti.tqs.shopbackend.model.Order;
import pt.ua.deti.tqs.shopbackend.model.OrderStatus;
import pt.ua.deti.tqs.shopbackend.model.PickUpLocation;
import pt.ua.deti.tqs.shopbackend.model.enums.Status;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PickUpService {
    private final OrderStatusRepository orderStatusRepository;
    List<PickUpLocation> pickUpLocations;

    private final PickupApi api;
    private final PickUpLocationRepository pickUpLocationRepository;
    private final PickUpServiceRepository pickUpServiceRepository;
    private final OrderRepository orderRepository;

    public List<PickUpLocation> getPickUpLocations() {
        log.info("PickUpService -- Get pick up locations -- request received");
        return pickUpLocations;
    }

    public boolean cancelOrder(UUID id) {
        log.info("PickUpService -- Cancel order -- request received");
        RoadRunnerGeneralEntity ent = api.changePackageState(id, Status.CANCELLED.name(), "==shop==");
        if (ent != null && ent.getMsg().equals("PACKAGE UPDATED")) {
            log.info("PickUpService -- Cancel order -- order cancelled");
            return true;
        }
        log.error("PickUpService -- Cancel order -- order not cancelled");
        return false;
    }

    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void updatePickupLocations() {
        log.info("PickUpService -- Update pick up locations -- on schedule");
        RoadRunnerPickupEntity ent = api.getPickupPoints();
        if (ent != null) {
            log.info("PickUpService -- Update pick up locations -- Pick up locations updated");
            pickUpLocations = ent.pickUpLocations;
            ent.pickUpLocations.forEach(pickUpLocation -> pickUpLocation.setPickUpService(pickUpServiceRepository.findBySlug("roadrunner").orElse(null)));
            pickUpLocationRepository.saveAll(pickUpLocations);
            return;
        }
        log.error("PickUpService -- Update pick up locations -- Pick up locations not updated");
    }

    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void fetchAllPackagesHistory() {
        log.info("PickUpService -- fetchAllPackagesHistory -- on schedule");
        RoadRunnerPackageHistoryEntity ent = api.fetchPackageHistory();

        if (ent != null) {
            log.info("PickUpService --  fetchAllPackagesHistory -- request sent");
            Map<UUID, List<OrderStatus>> map = ent.getStatuses();
            for (UUID id : map.keySet()) {
                List<OrderStatus> statuses = map.get(id);
                Order order = orderRepository.findByTrackingId(id).orElse(null);
                if (order != null) {
                    order.getOrderStatus().clear();
                    order.getOrderStatus().addAll(statuses);
                    order.sort();
                    order.setStatus(statuses.get(statuses.size() - 1).getStatus());
                    orderRepository.save(order);
                }
            }
            log.info("PickUpService -- fetchAllPackagesHistory -- request successful");
        } else {
            log.error("PickUpService -- fetchAllPackagesHistory -- request failed");
        }
    }
}

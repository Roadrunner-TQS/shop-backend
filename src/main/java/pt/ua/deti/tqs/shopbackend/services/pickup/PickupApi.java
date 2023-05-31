package pt.ua.deti.tqs.shopbackend.services.pickup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.UUID;

// TODO LOGS
@Slf4j
@Service
public class PickupApi {
    private final PickupExtApi api;

    public PickupApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        this.api = retrofit.create(PickupExtApi.class);
    }

    public RoadRunnerPickupEntity getPickupPoints() {
        log.info("PickupApi -- Get pickup points -- request received");
        try {
            log.info("PickupApi -- Get pickup points -- request sent");
            return api.getPickupLocation().execute().body();
        } catch (IOException e) {
            log.error("PickupApi -- Get pickup points -- " + e.getMessage());
            return null;
        }
    }

    public RoadRunnerPackageEntity createPackage(PackDto pkg) {
        log.info("PickupApi -- Create package -- request received");
        try {
            log.info("PickupApi -- Create package -- request sent");
            return api.createPackage(pkg).execute().body();
        } catch (IOException e) {
            log.error("PickupApi -- Create package -- " + e.getMessage());
            return null;
        }
    }

    public RoadRunnerGeneralEntity changePackageState(UUID id, String newState, String token) {
        log.info("PickupApi -- Change package state -- request received");
        try {
            log.info("PickupApi -- Change package state -- request sent");
            return api.changePackageState(id, newState, token).execute().body();
        } catch (IOException e) {
            log.error("PickupApi -- Change package state -- " + e.getMessage());
            return null;
        }
    }

    public RoadRunnerPackageHistoryEntity fetchPackageHistory() {
        log.info("PickupApi -- Change package state -- request received");
        try {
            log.info("PickupApi -- Change package state -- request sent");
            return api.fetchPackageHistory().execute().body();
        } catch (IOException e) {
            log.error("PickupApi -- Change package state -- " + e.getMessage());
            return null;
        }
    }
}

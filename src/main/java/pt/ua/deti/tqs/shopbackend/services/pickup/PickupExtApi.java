package pt.ua.deti.tqs.shopbackend.services.pickup;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.UUID;

public interface PickupExtApi {
    @GET("api/shop/pickuplocation")
    Call<RoadRunnerPickupEntity> getPickupLocation();

    @POST("api/shop/package")
    Call<RoadRunnerPackageEntity> createPackage(
            @Body PackDto pkg
    );

    @PUT("api/shop/package/{id}")
    Call<RoadRunnerGeneralEntity> changePackageState(
            @Path("id") UUID id,
            @Query("newstate") String newState,
            @Header("Authorization") String token
    );

    @GET("/api/shop/history")
    Call<RoadRunnerPackageHistoryEntity> fetchPackageHistory();
}

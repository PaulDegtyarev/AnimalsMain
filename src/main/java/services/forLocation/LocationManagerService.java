package services.forLocation;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import persistance.ForLocation.LocationPersistence;

public interface LocationManagerService {

    static LocationManagerService create(LocationPersistence locationPersistence){return new LocationManagerServiceImpl(locationPersistence);}

    boolean checkLocationId(String locationParam);

    boolean emptyOrNot(Integer idForSearch);

    boolean dataIsValid(JsonObject info);

    boolean coordIsFree(JsonObject info);

    void getLocationById(Integer locationId,
                         ServiceRequest request,
                         Handler<AsyncResult<ServiceResponse>> resultHandler);

    void addLocation(JsonObject info,
                     ServiceRequest request,
                     Handler<AsyncResult<ServiceResponse>> resultHandler);
}

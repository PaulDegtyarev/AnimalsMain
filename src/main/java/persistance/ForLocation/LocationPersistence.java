package persistance.ForLocation;

import io.vertx.core.json.JsonObject;
import models.ForLocation.Location;

import java.util.Optional;

public interface LocationPersistence {

    static LocationPersistence create(){
        return new LocationPersistenceImpl();
    }

    Optional<JsonObject> getLocationById(Integer locationId);

    Optional<Location> addLocation(JsonObject infoAboutNewLocation);
}

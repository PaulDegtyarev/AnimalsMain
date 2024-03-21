package persistance.ForAreas;

import io.vertx.core.json.JsonObject;

import java.util.Optional;

public interface AreasPersistance {
    static AreasPersistance create(){return new AreasPersistanceImpl();}

    Optional<JsonObject> getAreaById(Integer areaId);
}
